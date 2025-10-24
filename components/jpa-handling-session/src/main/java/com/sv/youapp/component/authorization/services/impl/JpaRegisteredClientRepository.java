package com.sv.youapp.component.authorization.services.impl;

import com.sv.youapp.component.authorization.entities.jpa.RegisteredClientEntity;
import com.sv.youapp.component.authorization.repositories.jpa.ClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.Locale;
import java.util.Optional;

public record JpaRegisteredClientRepository(ClientRepository clientRepository) implements RegisteredClientRepository {

	@Override
	public void save(RegisteredClient registeredClient) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public RegisteredClient findById(String id) {
		if (id == null || id.isBlank()) return null;
		Optional<RegisteredClientEntity> opt = clientRepository.findById(id);
		return opt.map(JpaRegisteredClientRepository::toRegisteredClient).orElse(null);
	}

	@Override
	public RegisteredClient findByClientId(String clientId) {
		if (clientId == null || clientId.isBlank()) return null;
		Optional<RegisteredClientEntity> opt = clientRepository.findByClientId(clientId);
		return opt.map(JpaRegisteredClientRepository::toRegisteredClient).orElse(null);
	}

	private static RegisteredClient toRegisteredClient(RegisteredClientEntity self) {
		RegisteredClient.Builder builder = RegisteredClient.withId(self.getId())
			.clientId(self.getClientId())
			.clientSecret(self.getClientSecret())
			.clientName(self.getClientName())
			.clientIdIssuedAt(self.getClientIdIssuedAt())
			.clientSecretExpiresAt(self.getClientSecretExpiresAt());

		// Authentication methods
		self.getAuthenticationMethods().forEach(method ->
			builder.clientAuthenticationMethod(new ClientAuthenticationMethod(method.getName()))
		);

		// Grant types
		self.getGrantTypes().forEach(grant ->
			builder.authorizationGrantType(new AuthorizationGrantType(grant.getName()))
		);

		// Redirect URIs
		self.getRedirectUris().forEach(uri ->
			builder.redirectUri(uri.getRedirectUri())
		);

		// Post-logout Redirect URIs
		self.getPostLogoutRedirectUris().forEach(uri ->
			builder.postLogoutRedirectUri(uri.getRedirectUri())
		);

		// Scopes
		self.getScopes().forEach(scope ->
			builder.scope(scope.getName())
		);

		if (self.getClientSettings() != null) {
			var cs = self.getClientSettings();
			ClientSettings.Builder csBuilder = ClientSettings.builder();
			csBuilder.requireProofKey(cs.getRequireProofKey());
			csBuilder.requireAuthorizationConsent(cs.getRequireAuthorizationConsent());
			builder.clientSettings(csBuilder.build());
		}

		if (self.getTokenSettings() != null) {
			var ts = self.getTokenSettings();
			TokenSettings.Builder tsBuilder = TokenSettings.builder();

			tsBuilder.accessTokenTimeToLive(ts.getAccessTokenTimeToLive());
			tsBuilder.reuseRefreshTokens(false);
			tsBuilder.refreshTokenTimeToLive(ts.getRefreshTokenTimeToLive());

			String fmt = ts.getAccessTokenFormat();
			if (fmt != null) {
				String f = fmt.toLowerCase(Locale.ROOT);
				if (f.equals("reference")) {
					tsBuilder.accessTokenFormat(OAuth2TokenFormat.REFERENCE);
				} else {
					tsBuilder.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED);
				}
			}
			builder.tokenSettings(tsBuilder.build());
		}
		return builder.build();
	}
}
