package com.sv.youapp.component.authorization.entities.redis

class OAuth2NativeGrantAuthorization(
    id: String,
    registeredClientId: String,
    principalName: String,
    scopes: String,
    accessToken: AccessToken?,
    refreshToken: RefreshToken?,
) : AbstractGrantAuthorization(
        id,
        registeredClientId,
        principalName,
        scopes,
        accessToken,
        refreshToken,
    )
