package com.felix.msauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.felix.msauth.model.Role;
import com.felix.msauth.model.enums.ERole;
import com.felix.msauth.repository.RoleRepository;

@Service
public class RoleService {
	
	@Autowired
	private RoleRepository repository;
	
	public Role findByName(ERole nome) {
		return repository.findByName(nome)
		          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	}

	

}
