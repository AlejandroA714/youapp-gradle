package com.sv.youapp.component.authorization.entities.redis;

public class OAuth2NativeGrantAuthorization extends AbstractGrantAuthorization {

	public OAuth2NativeGrantAuthorization(
		String id,
		String registeredClientId,
		String principalName,
		String scopes,
		AbstractGrantAuthorization.AccessToken accessToken,
		AbstractGrantAuthorization.RefreshToken refreshToken
	) {
		super(id, registeredClientId, principalName, scopes, accessToken, refreshToken);
	}
}
