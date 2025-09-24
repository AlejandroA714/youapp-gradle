package com.sv.youapp.authorization.entities.redis

import org.springframework.data.redis.core.index.Indexed
import java.security.Principal
import java.time.Instant

class OAuth2DeviceCodeGrantAuthorization(
    id: String?,
    registeredClientId: String?,
    principalName: String,
    scopes: String,
    accessToken: AccessToken?,
    refreshToken: RefreshToken?,
    val principal: Principal?,
    val deviceCode: DeviceCode?,
    val userCode: UserCode?,
    val requestedScopes: String,
    @Indexed
    val deviceState: String?,
) : AbstractGrantAuthorization(id, registeredClientId, principalName, scopes, accessToken, refreshToken) {
    class DeviceCode(tokenValue: String?, issuedAt: Instant?, expiresAt: Instant?, invalidated: Boolean) : AbstractToken(tokenValue, issuedAt, expiresAt, invalidated)

    class UserCode(tokenValue: String?, issuedAt: Instant?, expiresAt: Instant?, invalidated: Boolean) : AbstractToken(tokenValue, issuedAt, expiresAt, invalidated)
}
