package com.sv.youapp.component.authorization.entities.redis;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;

import java.time.Instant;
import java.util.Map;

@Getter
@RedisHash("oauth2_authorization")
public abstract class AbstractGrantAuthorization {

    @Id
    private final String id;

    private final String registeredClientId;

    private final String principalName;

    private final String scopes;

    private final AccessToken accessToken;

    private final RefreshToken refreshToken;


    public AbstractGrantAuthorization(
    String id,
    String registeredClientId,
    String principalName,
    String scopes,
    AccessToken accessToken,
    RefreshToken refreshToken
    ) {
        this.id = id;
        this.registeredClientId = registeredClientId;
        this.principalName = principalName;
        this.scopes = scopes;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

	@Getter
	public static abstract class AbstractToken {

        @Indexed
        private final String tokenValue;

        private final Instant issuedAt;

        private final Instant expiresAt;

        private final boolean invalidated;

        public AbstractToken(
        String tokenValue,
        Instant issuedAt,
        Instant expiresAt,
        boolean invalidated
        ) {
            this.tokenValue = tokenValue;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
            this.invalidated = invalidated;
        }

	}

    @Getter
	public static final class AccessToken extends AbstractToken {

        private final OAuth2AccessToken.TokenType tokenType;

        private final String scopes;

        private final OAuth2TokenFormat tokenFormat;

        private final ClaimsHolder claims;

        public AccessToken(
                String tokenValue,
        Instant issuedAt,
        Instant expiresAt,
        boolean invalidated,
        OAuth2AccessToken.TokenType tokenType,
        String scopes,
        OAuth2TokenFormat tokenFormat,
        ClaimsHolder claims
        ) {
            super(tokenValue, issuedAt, expiresAt, invalidated);
            this.tokenType = tokenType;
            this.scopes = scopes;
            this.tokenFormat = tokenFormat;
            this.claims = claims;
        }

	}

    public static final class RefreshToken extends AbstractToken {

        public RefreshToken(
                String tokenValue,
        Instant issuedAt,
        Instant expiresAt,
        boolean invalidated
        ) {
            super(tokenValue, issuedAt, expiresAt, invalidated);
        }
    }

	public record ClaimsHolder(Map<String, Object> claims) { }
}
