package com.sv.youapp.component.authorization.services.impl;

import com.sv.youapp.common.authorization.services.NativeUserDetails;
import com.sv.youapp.component.authorization.repositories.jpa.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

public record JpaNativeUserDetails(UserRepository repository) implements NativeUserDetails {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = repository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));


		//TODO: CHECK WHAT EXACTLY USER DTO DOES
		// var dto = new UserDTO();
//        dto.setUsername(user.getUsername());
//        dto.setAuthorities(
//            user.getAuthorities().stream()
//                .map(a -> a.getName())
//        .collect(Collectors.toList())
//        );

		var authorities = user.getAuthorities().stream()
			.map(a -> new SimpleGrantedAuthority(a.getName()))
			.collect(Collectors.toSet());

		return User
			.withUsername(user.getUsername())
			.password(user.getPassword())
			.authorities(authorities)
			.accountLocked(!Boolean.TRUE.equals(user.getEnabled()))
			.build();
	}
}

