package com.sv.youapp.component.authorization.providers;

import com.sv.youapp.common.authorization.authentication.NativeAuthentication;
import com.sv.youapp.common.authorization.services.NativeAuthenticationService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sv.youapp.common.authorization.authentication.NativeAuthentication.NATIVE_GRANT_TYPE;

public class NativeAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final NativeAuthenticationService nativeAuthenticationService;

    public NativeAuthenticationProvider(
            OAuth2AuthorizationService authorizationService,
    OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
    NativeAuthenticationService nativeAuthenticationService
    ) {
        this.authorizationService = Objects.requireNonNull(authorizationService);
        this.tokenGenerator = Objects.requireNonNull(tokenGenerator);
        this.nativeAuthenticationService = Objects.requireNonNull(nativeAuthenticationService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        NativeAuthentication nativeAuthentication = (NativeAuthentication) authentication;

        OAuth2ClientAuthenticationToken clientPrincipal =
        (OAuth2ClientAuthenticationToken) nativeAuthentication.getClientPrincipal();
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (!registeredClient.getAuthorizationGrantTypes().contains(NATIVE_GRANT_TYPE)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        Set<String> requestedScopes = nativeAuthentication.getScopes()
            .stream().map(Object::toString).collect(Collectors.toSet());

        if (!requestedScopes.isEmpty()
            && !registeredClient.getScopes().containsAll(requestedScopes)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodes.INVALID_REQUEST,
            "OAuth 2.0 Parameter: " + OAuth2ParameterNames.SCOPE,
            null
            )
            );
        }

        // DO NECESSARY STUFF, GET FROM DB, GET ROLES, AND PUT IN
        UserDetails user = nativeAuthenticationService.authenticate(authentication);
		//TODO: REVISATE
        nativeAuthentication.setGranted(new HashSet<>(user.getAuthorities()));
        //
        nativeAuthentication.setAuthenticated(true);

        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
            .registeredClient(registeredClient)
            .principal(nativeAuthentication)
            .authorizationServerContext(AuthorizationServerContextHolder.getContext())
            .authorizedScopes(requestedScopes)
            .authorizationGrantType(NATIVE_GRANT_TYPE)
            .authorizationGrant(nativeAuthentication);

        // ----- Access token -----
        OAuth2TokenContext tokenContext =
        tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();

        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        if (generatedAccessToken == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodes.SERVER_ERROR,
            "The token generator failed to generate the access token.",
            "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2"
            )
            );
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            generatedAccessToken.getTokenValue(),
            generatedAccessToken.getIssuedAt(),
            generatedAccessToken.getExpiresAt(),
            requestedScopes
        );

        // ----- Refresh token -----
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (generatedRefreshToken != null) {
                if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                    throw new OAuth2AuthenticationException(
                            new OAuth2Error(
                                    OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate a valid refresh token.",
                    "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2"
                    )
                    );
                }
                refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            }
        }

        OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient)
            .principalName((String) nativeAuthentication.getPrincipal())
            .authorizationGrantType(NATIVE_GRANT_TYPE);

        if (generatedAccessToken instanceof ClaimAccessor) {
            ClaimAccessor accessor = (ClaimAccessor) generatedAccessToken;
            authorizationBuilder.token(accessToken, metadata ->
            metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, accessor.getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        if (refreshToken != null) {
            authorizationBuilder.refreshToken(refreshToken);
        }

        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient,
        clientPrincipal,
        accessToken,
        refreshToken,
        Collections.emptyMap()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication != null && NativeAuthentication.class.isAssignableFrom(authentication);
    }
}

