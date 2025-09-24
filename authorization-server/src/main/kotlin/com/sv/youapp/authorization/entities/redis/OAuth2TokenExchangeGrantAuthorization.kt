package com.sv.youapp.authorization.entities.redis

class OAuth2TokenExchangeGrantAuthorization(
    id: String?,
    registeredClientId: String?,
    principalName: String,
    scopes: String,
    accessToken: AccessToken?,
) : AbstractGrantAuthorization(id, registeredClientId, principalName, scopes, accessToken, null)
