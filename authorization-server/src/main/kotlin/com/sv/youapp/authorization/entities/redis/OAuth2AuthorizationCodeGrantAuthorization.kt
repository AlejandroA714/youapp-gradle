package com.sv.youapp.authorization.entities.redis

import org.springframework.data.redis.core.index.Indexed
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import java.security.Principal
import java.time.Instant

open class OAuth2AuthorizationCodeGrantAuthorization(
    id: String,
    registeredClientId: String,
    principalName: String,
    scopes: String,
    accessToken: AccessToken?,
    refreshToken: RefreshToken?,
    val principal: Principal?,
    val authorizationRequest: OAuth2AuthorizationRequest?,
    val authorizationCode: AuthorizationCode?,
    @Indexed
    val state: String?,
) : AbstractGrantAuthorization(id, registeredClientId, principalName, scopes, accessToken, refreshToken) {

    class AuthorizationCode(tokenValue: String?, issuedAt: Instant?, expiresAt: Instant?, invalidated: Boolean) :
        AbstractToken(tokenValue, issuedAt, expiresAt, invalidated)
}
