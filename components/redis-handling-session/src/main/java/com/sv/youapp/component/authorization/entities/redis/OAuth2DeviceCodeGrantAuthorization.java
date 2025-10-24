package com.sv.youapp.component.authorization.entities.redis;

import lombok.Getter;
import org.springframework.data.redis.core.index.Indexed;

import java.security.Principal;
import java.time.Instant;

@Getter
public class OAuth2DeviceCodeGrantAuthorization extends AbstractGrantAuthorization {

	private final Principal principal;
	private final DeviceCode deviceCode;
	private final UserCode userCode;
	private final String requestedScopes;

	@Indexed
	private final String deviceState;

	public OAuth2DeviceCodeGrantAuthorization(
		String id,
		String registeredClientId,
		String principalName,
		String scopes,
		AccessToken accessToken,
		RefreshToken refreshToken,
		Principal principal,
		DeviceCode deviceCode,
		UserCode userCode,
		String requestedScopes,
		String deviceState
	) {
		super(id, registeredClientId, principalName, scopes, accessToken, refreshToken);
		this.principal = principal;
		this.deviceCode = deviceCode;
		this.userCode = userCode;
		this.requestedScopes = requestedScopes;
		this.deviceState = deviceState;
	}

	public static final class DeviceCode extends AbstractToken {
		public DeviceCode(String tokenValue, Instant issuedAt, Instant expiresAt, boolean invalidated) {
			super(tokenValue, issuedAt, expiresAt, invalidated);
		}
	}

	public static final class UserCode extends AbstractToken {
		public UserCode(String tokenValue, Instant issuedAt, Instant expiresAt, boolean invalidated) {
			super(tokenValue, issuedAt, expiresAt, invalidated);
		}
	}
}
