package com.sv.youapp.authorization.mapper

import com.sv.youapp.authorization.entities.RegisteredClientEntity
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings

fun RegisteredClientEntity.toRegisteredClient(): RegisteredClient {
    val builder =
        RegisteredClient.withId(this.id.toString())
            .clientId(this.clientId)
            .clientSecret(this.clientSecret)
            .clientName(this.clientName)
            .clientIdIssuedAt(this.clientIdIssuedAt)
            .clientSecretExpiresAt(this.clientSecretExpiresAt)

    // Authentication methods
    this.authenticationMethods.forEach { method ->
        builder.clientAuthenticationMethod(ClientAuthenticationMethod(method.name))
    }

    // Grant types
    this.grantTypes.forEach { grant ->
        builder.authorizationGrantType(AuthorizationGrantType(grant.name))
    }

    // Redirect URIs
    this.redirectUris.forEach { uri ->
        builder.redirectUri(uri.redirectUri)
    }
    // Post URIs
    this.postLogoutRedirectUris.forEach { uri ->
        builder.postLogoutRedirectUri(uri.redirectUri)
    }

    // Scopes
    this.scopes.forEach { scope ->
        builder.scope(scope.name)
    }

    this.clientSettings?.let { cs ->
        val csBuilder = ClientSettings.builder()
        cs.requireProofKey.let { csBuilder.requireProofKey(it) }
        cs.requireAuthorizationConsent.let { csBuilder.requireAuthorizationConsent(it) }
        builder.clientSettings(csBuilder.build())
    }

    this.tokenSettings?.let { ts ->
        val tsBuilder = TokenSettings.builder()

        ts.accessTokenTtl.let { tsBuilder.accessTokenTimeToLive(it) }
        ts.reuseRefreshTokens.let { tsBuilder.reuseRefreshTokens(it) }
        ts.refreshTokenTtl.let { tsBuilder.refreshTokenTimeToLive(it) }

        ts.accessTokenFormat.let { fmt ->
            when (fmt.lowercase()) {
                "self-contained", "jwt" -> tsBuilder.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                "reference" -> tsBuilder.accessTokenFormat(OAuth2TokenFormat.REFERENCE)
            }
        }
        builder.tokenSettings(tsBuilder.build())
    }
    return builder.build()
}
