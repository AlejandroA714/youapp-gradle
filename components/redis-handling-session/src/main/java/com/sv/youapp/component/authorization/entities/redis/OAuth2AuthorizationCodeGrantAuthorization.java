package com.sv.youapp.component.authorization.entities.redis;

import lombok.Getter;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.security.Principal;
import java.time.Instant;

@Getter
public class OAuth2AuthorizationCodeGrantAuthorization extends AbstractGrantAuthorization {

	private final Principal principal;
	private final OAuth2AuthorizationRequest authorizationRequest;
	private final AuthorizationCode authorizationCode;

	@Indexed
	private final String state;

	public OAuth2AuthorizationCodeGrantAuthorization(
		String id,
		String registeredClientId,
		String principalName,
		String scopes,
		AccessToken accessToken,
		RefreshToken refreshToken,
		Principal principal,
		OAuth2AuthorizationRequest authorizationRequest,
		AuthorizationCode authorizationCode,
		String state
	) {
		super(id, registeredClientId, principalName, scopes, accessToken, refreshToken);
		this.principal = principal;
		this.authorizationRequest = authorizationRequest;
		this.authorizationCode = authorizationCode;
		this.state = state;
	}

	public static final class AuthorizationCode extends AbstractToken {
		public AuthorizationCode(
			String tokenValue,
			Instant issuedAt,
			Instant expiresAt,
			boolean invalidated
		) {
			super(tokenValue, issuedAt, expiresAt, invalidated);
		}
	}
}
