package com.sv.youapp.authorization.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.time.Duration
import java.util.UUID

@Configuration
@Profile("!jdbc")
class AuthorizationServerLocalConfig {
    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val oidcClient =
            RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret("\$2a\$12\$q1rY2JnWSH/xF/bGvnEM7eTbCjRfTxRw7gDd2DWZ/AeKPNfCWCvHq")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType("urn:ietf:params:oauth:grant-type:native"))
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://192.168.1.24:8081/oauth2/callback")
                .build()
        return InMemoryRegisteredClientRepository(oidcClient)
    }

    @Bean
    fun authorizationService(): OAuth2AuthorizationService {
        return InMemoryOAuth2AuthorizationService()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val user =
            User
                .builder()
                .username("user")
                .password("$2a$12\$tTybMgef5ZYYRzIpOzqEEuO9wAj76FnMogVgL0CO0mYFYRW0Wl7/C")
                .build()
        return InMemoryUserDetailsManager(user)
    }
}
