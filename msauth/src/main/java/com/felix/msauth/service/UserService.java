package com.felix.msauth.service;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.security.core.token.Token;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.felix.msauth.config.auth.jwt.JwtUtils;
import com.felix.msauth.dto.auth.PasswordTokenPublicData;
import com.felix.msauth.dto.auth.request.SignupRequest;
import com.felix.msauth.model.Role;
import com.felix.msauth.model.User;
import com.felix.msauth.model.enums.ERole;
import com.felix.msauth.repository.UserRepository;
import com.felix.msauth.service.exception.ObjectNotFoundException;

import lombok.SneakyThrows;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;
	
	public User getUserPorEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new ObjectNotFoundException(
				"Email não cadastrado! email: " + email + ", Tipo: "));
	}

	public User registerUser(User user, Set<String> strRoles) {

		if (userRepository.existsByUsername(user.getUsername())) {
//		      
			throw new ObjectNotFoundException("Error: Nnome do usuario já cadastrado! nome: " + user.getUsername()
					+ ", Tipo: " + User.class.getName());
		}
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new ObjectNotFoundException("Error: Email já cadastrado! Email: " + user.getEmail()
			+ ", Tipo: " + User.class.getName());
		    }

		user.setRoles(roles(strRoles));

		return userRepository.save(user);
	}
	
	
	private Set<Role> roles(Set<String> strRoles){
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleService.findByName(ERole.ROLE_USER);
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleService.findByName(ERole.ROLE_ADMIN);
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleService.findByName(ERole.ROLE_MODERATOR);
					roles.add(modRole);

					break;
				default:
					Role userRole = roleService.findByName(ERole.ROLE_USER);
					roles.add(userRole);
				}
			});
		}
		
		return roles;
	}

	public User fromDTO(SignupRequest obj) {
		User user = new User();
		user.setUsername(obj.getUsername());
		user.setEmail(obj.getEmail());
		user.setPassword(encoder.encode(obj.getPassword()));
		return user;
	}

	public User update(User user, Set<String> strRoles) {
			User newUser = userRepository.findByUsername(user.getUsername()).orElseThrow();
			
			updateData(newUser, user);
			newUser.setRoles(roles(strRoles));
			
			return userRepository.save(newUser);
			
	}

	private void updateData(User newUser, User user) {
		newUser.setUsername(user.getUsername());
		newUser.setEmail(user.getEmail());
		newUser.setPassword(encoder.encode(user.getPassword()));
	}
	
	public User getAuthenticatedUser() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }
	
	@SneakyThrows
	public String generateToken(User user) {
		KeyBasedPersistenceTokenService tokenService = getInstanceFor(user);
		
		Token token = tokenService.allocateToken(user.getEmail());
		
		return token.getKey();
	}
	
	private KeyBasedPersistenceTokenService getInstanceFor(User user) throws Exception {
		KeyBasedPersistenceTokenService tokenService = new KeyBasedPersistenceTokenService();
		
		tokenService.setServerSecret(user.getPassword());
		tokenService.setServerInteger(16);
		tokenService.setSecureRandom(new SecureRandomFactoryBean().getObject());
		return tokenService;
	}
	
	@SneakyThrows
	public void changePassword(String newPassword, String rawToken) {
		PasswordTokenPublicData publicData = readPublicData(rawToken);
		
//		if(isExpired())
		
		User user = userRepository.findByEmail(publicData.getEmail())
					.orElseThrow();
		
		KeyBasedPersistenceTokenService tokenService = this.getInstanceFor(user);
		tokenService.verifyToken(rawToken);
		
		user.setPassword(encoder.encode(newPassword));
		userRepository.save(null);
	}

	private PasswordTokenPublicData readPublicData(String rawToken) {
		String rawTokenDecoded = new String(Base64.getDecoder().decode(rawToken));
		String[] tokenParts = rawTokenDecoded.split(":");
		Long timestamp = Long.parseLong(tokenParts[0]);
		String email = tokenParts[2];
		
		return new PasswordTokenPublicData(email, timestamp);
	}

}
