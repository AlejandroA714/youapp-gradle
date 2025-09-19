package com.sv.youapp.authorization.configuration

import com.sv.youapp.authorization.repositories.ClientRepository
import com.sv.youapp.authorization.repositories.UserRepository
import com.sv.youapp.authorization.services.impl.DefaultNativeUserDetails
import com.sv.youapp.authorization.services.impl.JpaRegisteredClientRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

@Configuration
@Profile("jdbc")
class AuthorizationServerConfig {
    @Bean
    fun userDetailsService(
        repository: UserRepository): UserDetailsService {
        return DefaultNativeUserDetails(repository)
    }

    @Bean
    fun registeredClientRepository(client: ClientRepository): RegisteredClientRepository {
        return JpaRegisteredClientRepository(client)
    }

    // TODO: MIGRATE REDIS
    @Bean
    fun authorizationService(): OAuth2AuthorizationService {
        return InMemoryOAuth2AuthorizationService()
    }
}
