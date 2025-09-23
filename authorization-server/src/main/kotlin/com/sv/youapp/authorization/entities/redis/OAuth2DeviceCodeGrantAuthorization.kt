package com.sv.youapp.authorization.entities.redis

import org.springframework.data.redis.core.index.Indexed
import java.security.Principal
import java.time.Instant

class OAuth2DeviceCodeGrantAuthorization(
    id: String?,
    registeredClientId: String?,
    principalName: String,
    authorizedScopes: MutableSet<String?>?,
    accessToken: AccessToken?,
    refreshToken: RefreshToken?,
    val principal: Principal? = null,

    val deviceCode: DeviceCode? = null,

    val userCode: UserCode? = null,

    val requestedScopes: MutableSet<String?>? = null,

    @Indexed
    val deviceState: String? = null): AbstractGrantAuthorization(id,registeredClientId, principalName, authorizedScopes, accessToken, refreshToken) {

    class DeviceCode(tokenValue: String?, issuedAt: Instant?, expiresAt: Instant?, invalidated: Boolean) : AbstractToken(tokenValue, issuedAt, expiresAt, invalidated)

    class UserCode(tokenValue: String?, issuedAt: Instant?, expiresAt: Instant?, invalidated: Boolean) : AbstractToken(tokenValue, issuedAt, expiresAt, invalidated)
}
