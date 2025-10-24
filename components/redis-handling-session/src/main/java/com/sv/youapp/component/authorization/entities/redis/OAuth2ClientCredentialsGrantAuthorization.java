package com.sv.youapp.component.authorization.entities.redis;

public class OAuth2ClientCredentialsGrantAuthorization extends AbstractGrantAuthorization {

	public OAuth2ClientCredentialsGrantAuthorization(
		String id,
		String registeredClientId,
		String principalName,
		String scopes,
		AbstractGrantAuthorization.AccessToken accessToken
	) {
		super(id, registeredClientId, principalName, scopes, accessToken, null);
	}
}
