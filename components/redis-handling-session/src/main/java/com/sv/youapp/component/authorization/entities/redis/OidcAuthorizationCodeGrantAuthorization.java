package com.sv.youapp.component.authorization.entities.redis;

import lombok.Getter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.security.Principal;
import java.time.Instant;

@Getter
public class OidcAuthorizationCodeGrantAuthorization extends OAuth2AuthorizationCodeGrantAuthorization {

	private final IdToken idToken;

	public OidcAuthorizationCodeGrantAuthorization(
		String id,
		String registeredClientId,
		String principalName,
		String scopes,
		AccessToken accessToken,
		RefreshToken refreshToken,
		Principal principal,
		OAuth2AuthorizationRequest authorizationRequest,
		AuthorizationCode authorizationCode,
		String state,
		IdToken idToken
	) {
		super(id, registeredClientId, principalName, scopes, accessToken, refreshToken, principal, authorizationRequest, authorizationCode, state);
		this.idToken = idToken;
	}

	@Getter
	public static final class IdToken extends AbstractToken {
		private final AbstractGrantAuthorization.ClaimsHolder claims;

		public IdToken(
			String tokenValue,
			Instant issuedAt,
			Instant expiresAt,
			boolean invalidated,
			AbstractGrantAuthorization.ClaimsHolder claims
		) {
			super(tokenValue, issuedAt, expiresAt, invalidated);
			this.claims = claims;
		}

	}
}
