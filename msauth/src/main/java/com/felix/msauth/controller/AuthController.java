package com.felix.msauth.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.felix.msauth.config.auth.jwt.JwtUtils;
import com.felix.msauth.dto.auth.request.LoginRequest;
import com.felix.msauth.dto.auth.request.SignupRequest;
import com.felix.msauth.dto.auth.response.MessageResponse;
import com.felix.msauth.dto.auth.response.UserInfoResponse;
import com.felix.msauth.model.User;
import com.felix.msauth.service.UserDetailsImpl;
import com.felix.msauth.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	public UserService service;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(
				new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		// Create new user's account
		User user = service.fromDTO(signUpRequest);
		user = service.registerUser(user, signUpRequest.getRole());

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateUser(@RequestBody SignupRequest signUpRequest) {
		User user = service.fromDTO(signUpRequest);
		user = service.update(user, signUpRequest.getRole());

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PutMapping("/forgot-password")
	public ResponseEntity<?> updatePassword(@RequestBody SignupRequest signUpRequest) throws Exception {
		service.forgotPassowrd(signUpRequest);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new MessageResponse("You've been signed out!"));
	}


}
