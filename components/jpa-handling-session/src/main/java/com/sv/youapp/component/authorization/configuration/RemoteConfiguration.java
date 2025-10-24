package com.sv.youapp.component.authorization.configuration;

import com.sv.youapp.common.authorization.services.NativeUserDetails;
import com.sv.youapp.component.authorization.repositories.jpa.ClientRepository;
import com.sv.youapp.component.authorization.repositories.jpa.UserRepository;
import com.sv.youapp.component.authorization.services.impl.JpaNativeUserDetails;
import com.sv.youapp.component.authorization.services.impl.JpaRegisteredClientRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@Configuration
@EntityScan(basePackages = "com.sv.youapp")
@EnableJpaRepositories(basePackages = "com.sv.youapp")
public class RemoteConfiguration {
	@Bean
	public NativeUserDetails jpaUserDetails(UserRepository userRepository) {
		return new JpaNativeUserDetails(userRepository);
	}

	@Bean
	public RegisteredClientRepository jpaRegisteredClientRepository(ClientRepository clientRepository) {
		return new JpaRegisteredClientRepository(clientRepository);
	}
}
