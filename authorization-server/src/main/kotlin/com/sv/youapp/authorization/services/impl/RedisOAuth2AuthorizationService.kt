package com.sv.youapp.authorization.services.impl

import org.springframework.lang.Nullable;
import com.sv.youapp.authorization.entities.redis.AbstractGrantAuthorization
import com.sv.youapp.authorization.mapper.convertOAuth2AuthorizationGrantAuthorization
import com.sv.youapp.authorization.mapper.mapOAuth2AuthorizationGrantAuthorization
import com.sv.youapp.authorization.repositories.redis.OAuth2AuthorizationGrantAuthorizationRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.util.Assert


class RedisOAuth2AuthorizationService(
    registeredClientRepository: RegisteredClientRepository,
    authorizationGrantAuthorizationRepository: OAuth2AuthorizationGrantAuthorizationRepository
) : OAuth2AuthorizationService {
    private val registeredClientRepository: RegisteredClientRepository

    private val authorizationGrantAuthorizationRepository: OAuth2AuthorizationGrantAuthorizationRepository

    init {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null")
        Assert.notNull(
            authorizationGrantAuthorizationRepository,
            "authorizationGrantAuthorizationRepository cannot be null"
        )
        this.registeredClientRepository = registeredClientRepository
        this.authorizationGrantAuthorizationRepository = authorizationGrantAuthorizationRepository
    }

    override fun save(authorization: OAuth2Authorization) {
        Assert.notNull(authorization, "authorization cannot be null")
        val authorizationGrantAuthorization: AbstractGrantAuthorization? = convertOAuth2AuthorizationGrantAuthorization(authorization)
        authorizationGrantAuthorization?.let { this.authorizationGrantAuthorizationRepository.save(it) }
    }

    override fun remove(authorization: OAuth2Authorization) {
        Assert.notNull(authorization, "authorization cannot be null")
        this.authorizationGrantAuthorizationRepository.deleteById(authorization.getId())
    }

    @Nullable
    override fun findById(id: String): OAuth2Authorization? {
        Assert.hasText(id, "id cannot be empty")
        return this.authorizationGrantAuthorizationRepository.findById(id)
            .map({ authorizationGrantAuthorization: AbstractGrantAuthorization -> this.toOAuth2Authorization(authorizationGrantAuthorization) })
            .orElse(null)
    }

    @Nullable
    override fun findByToken(token: String?, tokenType: OAuth2TokenType?): OAuth2Authorization? {
        Assert.hasText(token, "token cannot be empty")
        var authorizationGrantAuthorization: AbstractGrantAuthorization? = null
        if (tokenType == null) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                .findByStateOrAuthorizationCode_TokenValue(token, token)
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                    .findByAccessToken_TokenValueOrRefreshToken_TokenValue(token, token)
            }
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                    .findByIdToken_TokenValue(token)
            }
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                    .findByDeviceStateOrDeviceCode_TokenValueOrUserCode_TokenValue(token, token, token)
            }
        } else if (OAuth2ParameterNames.STATE == tokenType.value) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByState(token)
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                    .findByDeviceState(token)
            }
        } else if (OAuth2ParameterNames.CODE == tokenType.value) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                .findByAuthorizationCode_TokenValue(token)
        } else if (OAuth2TokenType.ACCESS_TOKEN == tokenType) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                .findByAccessToken_TokenValue(token)
        } else if (OidcParameterNames.ID_TOKEN == tokenType.value) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                .findByIdToken_TokenValue(token)
        } else if (OAuth2TokenType.REFRESH_TOKEN == tokenType) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                .findByRefreshToken_TokenValue(token)
        } else if (OAuth2ParameterNames.USER_CODE == tokenType.value) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                .findByUserCode_TokenValue(token)
        } else if (OAuth2ParameterNames.DEVICE_CODE == tokenType.value) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                .findByDeviceCode_TokenValue(token)
        }
        return if (authorizationGrantAuthorization != null) toOAuth2Authorization(authorizationGrantAuthorization) else null
    }

    private fun toOAuth2Authorization(
        authorizationGrantAuthorization: AbstractGrantAuthorization
    ): OAuth2Authorization? {
        val registeredClient = this.registeredClientRepository
            .findById(authorizationGrantAuthorization.registeredClientId)
        val builder = OAuth2Authorization.withRegisteredClient(registeredClient)
        mapOAuth2AuthorizationGrantAuthorization(authorizationGrantAuthorization, builder)
        return builder.build()
    }
}
