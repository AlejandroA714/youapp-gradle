package com.sv.youapp.component.authorization.services.impl

import com.sv.youapp.component.authorization.entities.jpa.RegisteredClientEntity
import com.sv.youapp.component.authorization.repositories.jpa.ClientRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings

class JpaRegisteredClientRepository
(private val clientRepository: ClientRepository) : RegisteredClientRepository {
    override fun save(registeredClient: RegisteredClient?) {
        // TODO("NOT IMPLEMENTED")
    }

    override fun findById(id: String?): RegisteredClient? {
        if (id.isNullOrBlank()) return null
        return clientRepository.findById(id)
            .map { it.toRegisteredClient() }
            .orElse(null)
    }

    override fun findByClientId(clientId: String?): RegisteredClient? {
        if (clientId.isNullOrBlank()) return null
        return clientRepository.findByClientId(clientId)
            .map { it.toRegisteredClient() }
            .orElse(null)
    }
}

private fun RegisteredClientEntity.toRegisteredClient(): RegisteredClient {
    val builder =
        RegisteredClient.withId(this.id)
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
