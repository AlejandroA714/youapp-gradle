package com.sv.youapp.authorization.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import java.time.Duration
import java.util.UUID

@Configuration
class ClientRegistrationConfiguration {

    //TODO: MIGRATED JDBC
    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val oidcClient =
            RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret("\$2a\$10\$FTAJRyxFIIyZQuIYflBOO.HXgElrorW6oB07/8eUA3kD2SbKVLRrG")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType("urn:ietf:params:oauth:grant-type:native"))
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauthdebugger.com/debug")
                .redirectUri("com.sv.youapp:/oauth2")
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.OPENID)
                .scope("offline_access")
                .tokenSettings(
                    TokenSettings.builder().reuseRefreshTokens(false)
                        .refreshTokenTimeToLive(Duration.ofDays(30)).build())
                .clientSettings(
                    ClientSettings.builder().requireProofKey(true)
                        .requireAuthorizationConsent(false).build(),
                )
                .build()
        return InMemoryRegisteredClientRepository(oidcClient)
    }
}
