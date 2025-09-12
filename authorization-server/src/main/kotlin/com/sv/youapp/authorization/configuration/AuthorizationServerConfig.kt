package com.sv.youapp.authorization.configuration

import com.sv.youapp.authorization.repositories.UserRepository
import com.sv.youapp.authorization.services.AuthenticationService
import com.sv.youapp.authorization.services.impl.DefaultAuthenticationService
import com.sv.youapp.authorization.services.impl.DefaultNativeUserDetails
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings


@Configuration
class AuthorizationServerConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(repository: UserRepository): UserDetailsService {
        return DefaultNativeUserDetails(repository)
    }

    //TODO: MIGRATE JDBC
    @Bean
    fun authenticationService(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder,
    ): AuthenticationService {
        return DefaultAuthenticationService(userDetailsService, passwordEncoder)
    }

    //TODO: MIGRATE JDBC
    @Bean
    fun authorizationService(): OAuth2AuthorizationService {
        return InMemoryOAuth2AuthorizationService()
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder()
            .issuer("https://f34a1d4ff096.ngrok-free.app")
            .build()
    }

    // / JDBC

//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        val user = User
//            .builder()
//            .username("user")
//            .password("$2a$10\$CBzc7daxUZ2lqKjEi3TtIeQNZmDj3Ko1nHUF6Ek.FiAkZdFWTTZzW") // hash puro
//            .roles("USER")
//            .build()
//
//        return InMemoryUserDetailsManager(user)
//    }
}
