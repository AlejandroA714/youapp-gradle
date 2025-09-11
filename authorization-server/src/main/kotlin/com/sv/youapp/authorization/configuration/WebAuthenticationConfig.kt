package com.sv.youapp.authorization.configuration

import com.sv.youapp.authorization.repositories.UserRepository
import com.sv.youapp.authorization.services.AuthenticationService
import com.sv.youapp.authorization.services.impl.DefaultAuthenticationService
import com.sv.youapp.authorization.services.impl.DefaultNativeUserDetails
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import java.util.UUID

private const val AUTHORITIES = "authorities"

@Configuration
class WebAuthenticationConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(repository: UserRepository): UserDetailsService {
        return DefaultNativeUserDetails(repository)
    }

    @Bean
    fun authenticationService(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder,
    ): AuthenticationService {
        return DefaultAuthenticationService(userDetailsService, passwordEncoder)
    }

    @Bean
    fun authorizationService(): OAuth2AuthorizationService {
        return InMemoryOAuth2AuthorizationService()
    }

    @Bean
    fun jwtTokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext?> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext? ->
            if (OAuth2TokenType.ACCESS_TOKEN == context!!.tokenType) {
                val principal: Authentication = context.getPrincipal()
                val authorities =
                    principal.authorities
                        ?.map { it.authority }
                        ?.toList()
                        ?: emptyList()
                context.claims.claims { claims ->
                    claims[AUTHORITIES] = authorities
                }
            }
        }
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val oidcClient =
            RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret("\$2a\$10\$FTAJRyxFIIyZQuIYflBOO.HXgElrorW6oB07/8eUA3kD2SbKVLRrG")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType("urn:ietf:params:oauth:grant-type:native"))
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauthdebugger.com/debug")
                .scope(OidcScopes.PROFILE)
                .clientSettings(
                    ClientSettings.builder().requireProofKey(true)
                        .requireAuthorizationConsent(false).build(),
                )
                .build()

        return InMemoryRegisteredClientRepository(oidcClient)
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
