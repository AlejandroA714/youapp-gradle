package com.sv.youapp.authorization.entities.redis

class OAuth2TokenExchangeGrantAuthorization(
    id: String?,
    registeredClientId: String?,
    principalName: String,
    authorizedScopes: MutableSet<String?>?,
    accessToken: AccessToken?,
    ): AbstractGrantAuthorization(id, registeredClientId, principalName, authorizedScopes, accessToken, null)
