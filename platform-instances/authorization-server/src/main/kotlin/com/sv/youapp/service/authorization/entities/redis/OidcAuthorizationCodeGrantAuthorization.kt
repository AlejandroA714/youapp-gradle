package com.sv.youapp.service.authorization.entities.redis

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import java.security.Principal
import java.time.Instant

class OidcAuthorizationCodeGrantAuthorization(
    id: String,
    registeredClientId: String,
    principalName: String,
    scopes: String,
    accessToken: AccessToken?,
    refreshToken: RefreshToken?,
    principal: Principal?,
    authorizationRequest: OAuth2AuthorizationRequest?,
    authorizationCode: AuthorizationCode?,
    state: String?,
    val idToken: IdToken?,
) : OAuth2AuthorizationCodeGrantAuthorization(id, registeredClientId, principalName, scopes, accessToken, refreshToken, principal, authorizationRequest, authorizationCode, state) {
    class IdToken(
        tokenValue: String?,
        issuedAt: Instant?,
        expiresAt: Instant?,
        invalidated: Boolean,
        val claims: ClaimsHolder?,
    ) : AbstractToken(tokenValue, issuedAt, expiresAt, invalidated)
}
