package com.sv.youapp.component.authorization.mapper;


import com.sv.youapp.component.authorization.entities.redis.AbstractGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OAuth2AuthorizationCodeGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OAuth2ClientCredentialsGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OAuth2DeviceCodeGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OAuth2NativeGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OAuth2TokenExchangeGrantAuthorization;
import com.sv.youapp.component.authorization.entities.redis.OidcAuthorizationCodeGrantAuthorization;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sv.youapp.common.authorization.authentication.NativeAuthentication.NATIVE_GRANT_TYPE;

public final class ModelMapper {

    private ModelMapper() {}

    public static AbstractGrantAuthorization convertOAuth2AuthorizationGrantAuthorization(OAuth2Authorization authorization) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorization.getAuthorizationGrantType())) {
            OAuth2AuthorizationRequest authorizationRequest =
            authorization.getAttribute(OAuth2AuthorizationRequest.class.getName());
            if (authorizationRequest != null && authorizationRequest.getScopes().contains(OidcScopes.OPENID)) {
                return convertOidcAuthorizationCodeGrantAuthorization(authorization);
            } else {
                return convertOAuth2AuthorizationCodeGrantAuthorization(authorization);
            }
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(authorization.getAuthorizationGrantType())) {
            return convertOAuth2ClientCredentialsGrantAuthorization(authorization);
        } else if (AuthorizationGrantType.DEVICE_CODE.equals(authorization.getAuthorizationGrantType())) {
            return convertOAuth2DeviceCodeGrantAuthorization(authorization);
        } else if (AuthorizationGrantType.TOKEN_EXCHANGE.equals(authorization.getAuthorizationGrantType())) {
            return convertOAuth2TokenExchangeGrantAuthorization(authorization);
        } else if (NATIVE_GRANT_TYPE.equals(authorization.getAuthorizationGrantType())) {
            return convertOAuth2NativeGrantAuthorization(authorization);
        }
        return null;
    }

    public static OAuth2NativeGrantAuthorization convertOAuth2NativeGrantAuthorization(OAuth2Authorization authorization) {
        AbstractGrantAuthorization.RefreshToken refreshToken = extractRefreshToken(authorization);
        AbstractGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        return new OAuth2NativeGrantAuthorization(
                authorization.getId(),
        authorization.getRegisteredClientId(),
        authorization.getPrincipalName(),
        toCsv(authorization.getAuthorizedScopes()),
        accessToken,
        refreshToken
        );
    }

    public static void mapOAuth2AuthorizationGrantAuthorization(
    AbstractGrantAuthorization authorizationGrantAuthorization,
    OAuth2Authorization.Builder builder
    ) {
        if (authorizationGrantAuthorization instanceof OidcAuthorizationCodeGrantAuthorization) {
            mapOidcAuthorizationCodeGrantAuthorization((OidcAuthorizationCodeGrantAuthorization) authorizationGrantAuthorization, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2AuthorizationCodeGrantAuthorization) {
            mapOAuth2AuthorizationCodeGrantAuthorization((OAuth2AuthorizationCodeGrantAuthorization) authorizationGrantAuthorization, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2ClientCredentialsGrantAuthorization) {
            mapOAuth2ClientCredentialsGrantAuthorization((OAuth2ClientCredentialsGrantAuthorization) authorizationGrantAuthorization, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2DeviceCodeGrantAuthorization) {
            mapOAuth2DeviceCodeGrantAuthorization((OAuth2DeviceCodeGrantAuthorization) authorizationGrantAuthorization, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2TokenExchangeGrantAuthorization) {
            mapOAuth2TokenExchangeGrantAuthorization((OAuth2TokenExchangeGrantAuthorization) authorizationGrantAuthorization, builder);
        } else if (authorizationGrantAuthorization instanceof OAuth2NativeGrantAuthorization) {
            mapOAuth2NativeGrantAuthorization((OAuth2NativeGrantAuthorization) authorizationGrantAuthorization, builder);
        }
    }

    public static void mapOAuth2NativeGrantAuthorization(
    OAuth2NativeGrantAuthorization authorizationGrantAuthorization,
    OAuth2Authorization.Builder builder
    ) {
        builder.id(authorizationGrantAuthorization.getId())
            .principalName(authorizationGrantAuthorization.getPrincipalName())
            .authorizationGrantType(NATIVE_GRANT_TYPE)
            .authorizedScopes(toStringSet(authorizationGrantAuthorization.getScopes()));
        mapAccessToken(authorizationGrantAuthorization.getAccessToken(), builder);
        mapRefreshToken(authorizationGrantAuthorization.getRefreshToken(), builder);
    }

    public static void mapOAuth2TokenExchangeGrantAuthorization(
    OAuth2TokenExchangeGrantAuthorization tokenExchangeGrantAuthorization,
    OAuth2Authorization.Builder builder
    ) {
        builder.id(tokenExchangeGrantAuthorization.getId())
            .principalName(tokenExchangeGrantAuthorization.getPrincipalName())
            .authorizationGrantType(AuthorizationGrantType.TOKEN_EXCHANGE)
            .authorizedScopes(toStringSet(tokenExchangeGrantAuthorization.getScopes()));
        mapAccessToken(tokenExchangeGrantAuthorization.getAccessToken(), builder);
    }

    public static String toCsv(Set<String> set) {
        return set == null ? "" : String.join(",", set);
    }

    public static Set<String> toStringSet(String csv) {
        if (!StringUtils.hasText(csv)) {
            return Collections.emptySet();
        }
        return Arrays.stream(csv.split(","))
            .map(String::trim)
            .collect(Collectors.toSet());
    }

    public static void mapOAuth2DeviceCodeGrantAuthorization(
    OAuth2DeviceCodeGrantAuthorization deviceCodeGrantAuthorization,
    OAuth2Authorization.Builder builder
    ) {
        builder.id(deviceCodeGrantAuthorization.getId())
            .principalName(deviceCodeGrantAuthorization.getPrincipalName())
            .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
            .authorizedScopes(toStringSet(deviceCodeGrantAuthorization.getScopes()));

        if (deviceCodeGrantAuthorization.getPrincipal() != null) {
            builder.attribute(Principal.class.getName(), deviceCodeGrantAuthorization.getPrincipal());
        }

        builder.attribute(OAuth2ParameterNames.SCOPE, toStringSet(deviceCodeGrantAuthorization.getRequestedScopes()));

        if (StringUtils.hasText(deviceCodeGrantAuthorization.getDeviceState())) {
            builder.attribute(OAuth2ParameterNames.STATE, deviceCodeGrantAuthorization.getDeviceState());
        }

        mapAccessToken(deviceCodeGrantAuthorization.getAccessToken(), builder);
        mapRefreshToken(deviceCodeGrantAuthorization.getRefreshToken(), builder);
        mapDeviceCode(deviceCodeGrantAuthorization.getDeviceCode(), builder);
        mapUserCode(deviceCodeGrantAuthorization.getUserCode(), builder);
    }

    public static void mapUserCode(
    OAuth2DeviceCodeGrantAuthorization.UserCode userCode,
    OAuth2Authorization.Builder builder
    ) {
        if (userCode == null) return;

        OAuth2UserCode oauth2UserCode =
        new OAuth2UserCode(
                userCode.getTokenValue(),
        userCode.getIssuedAt(),
        userCode.getExpiresAt()
        );

        builder.token(oauth2UserCode, metadata -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, userCode.isInvalidated());
        });
    }

    public static void mapDeviceCode(
    OAuth2DeviceCodeGrantAuthorization.DeviceCode deviceCode,
    OAuth2Authorization.Builder builder
    ) {
        if (deviceCode == null) return;

        OAuth2DeviceCode oauth2DeviceCode =
        new OAuth2DeviceCode(
                deviceCode.getTokenValue(),
        deviceCode.getIssuedAt(),
        deviceCode.getExpiresAt()
        );

        builder.token(oauth2DeviceCode, metadata -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, deviceCode.isInvalidated());
        });
    }

    public static void mapOAuth2ClientCredentialsGrantAuthorization(
    OAuth2ClientCredentialsGrantAuthorization clientCredentialsGrantAuthorization,
    OAuth2Authorization.Builder builder
    ) {
        builder.id(clientCredentialsGrantAuthorization.getId())
            .principalName(clientCredentialsGrantAuthorization.getPrincipalName())
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .authorizedScopes(toStringSet(clientCredentialsGrantAuthorization.getScopes()));

        mapAccessToken(clientCredentialsGrantAuthorization.getAccessToken(), builder);
    }

    public static void mapOidcAuthorizationCodeGrantAuthorization(
    OidcAuthorizationCodeGrantAuthorization authorizationCodeGrantAuthorization,
    OAuth2Authorization.Builder builder
    ) {
        mapOAuth2AuthorizationCodeGrantAuthorization(authorizationCodeGrantAuthorization, builder);
        mapIdToken(authorizationCodeGrantAuthorization.getIdToken(), builder);
    }

    public static void mapIdToken(
    OidcAuthorizationCodeGrantAuthorization.IdToken idToken,
    OAuth2Authorization.Builder builder
    ) {
        if (idToken == null) return;

        OidcIdToken oidcIdToken =
        new OidcIdToken(
                idToken.getTokenValue(),
        idToken.getIssuedAt(),
        idToken.getExpiresAt(),
        idToken.getClaims() == null ? null : idToken.getClaims().claims()
        );

        builder.token(oidcIdToken, metadata -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, idToken.isInvalidated());
            metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                idToken.getClaims() == null ? null : idToken.getClaims().claims());
        });
    }

    public static void mapOAuth2AuthorizationCodeGrantAuthorization(
    OAuth2AuthorizationCodeGrantAuthorization authorizationCodeGrantAuthorization,
    OAuth2Authorization.Builder builder
    ) {
        OAuth2AuthorizationRequest req = authorizationCodeGrantAuthorization.getAuthorizationRequest();
        OAuth2AuthorizationRequest safeReq = OAuth2AuthorizationRequest
                .from(req)
            .attributes(req.getAttributes())
        .additionalParameters(req.getAdditionalParameters())
        .build();

        builder.id(authorizationCodeGrantAuthorization.getId())
            .principalName(authorizationCodeGrantAuthorization.getPrincipalName())
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizedScopes(toStringSet(authorizationCodeGrantAuthorization.getScopes()))
            .attribute(Principal.class.getName(), authorizationCodeGrantAuthorization.getPrincipal())
            .attribute(OAuth2AuthorizationRequest.class.getName(), safeReq);

        if (StringUtils.hasText(authorizationCodeGrantAuthorization.getState())) {
            builder.attribute(OAuth2ParameterNames.STATE, authorizationCodeGrantAuthorization.getState());
        }

        mapAuthorizationCode(authorizationCodeGrantAuthorization.getAuthorizationCode(), builder);
        mapAccessToken(authorizationCodeGrantAuthorization.getAccessToken(), builder);
        mapRefreshToken(authorizationCodeGrantAuthorization.getRefreshToken(), builder);
    }

    public static void mapRefreshToken(
    AbstractGrantAuthorization.RefreshToken refreshToken,
    OAuth2Authorization.Builder builder
    ) {
        if (refreshToken == null) return;

        OAuth2RefreshToken oauth2RefreshToken =
        new OAuth2RefreshToken(
                refreshToken.getTokenValue(),
        refreshToken.getIssuedAt(),
        refreshToken.getExpiresAt()
        );

        builder.token(oauth2RefreshToken, metadata -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, refreshToken.isInvalidated());
        });
    }

    public static void mapAuthorizationCode(
    OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode,
    OAuth2Authorization.Builder builder
    ) {
        if (authorizationCode == null) return;

        OAuth2AuthorizationCode oauth2AuthorizationCode =
        new OAuth2AuthorizationCode(
                authorizationCode.getTokenValue(),
        authorizationCode.getIssuedAt(),
        authorizationCode.getExpiresAt()
        );

        builder.token(oauth2AuthorizationCode, metadata -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, authorizationCode.isInvalidated());
        });
    }

    public static void mapAccessToken(
    AbstractGrantAuthorization.AccessToken accessToken,
    OAuth2Authorization.Builder builder
    ) {
        if (accessToken == null) return;

        OAuth2AccessToken oauth2AccessToken =
        new OAuth2AccessToken(
                accessToken.getTokenType(),
        accessToken.getTokenValue(),
        accessToken.getIssuedAt(),
        accessToken.getExpiresAt(),
        toStringSet(accessToken.getScopes())
        );

        builder.token(oauth2AccessToken, metadata -> {
            metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, accessToken.isInvalidated());
            metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                accessToken.getClaims() == null ? null : accessToken.getClaims().claims());
            metadata.put(OAuth2TokenFormat.class.getName(),
                accessToken.getTokenFormat() == null ? null : accessToken.getTokenFormat().getValue());
        });
    }

    public static OAuth2AuthorizationCodeGrantAuthorization convertOAuth2AuthorizationCodeGrantAuthorization(
    OAuth2Authorization authorization
    ) {
        OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode = extractAuthorizationCode(authorization);
        AbstractGrantAuthorization.RefreshToken refreshToken = extractRefreshToken(authorization);
        AbstractGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);

        return new OAuth2AuthorizationCodeGrantAuthorization(
                authorization.getId(),
        authorization.getRegisteredClientId(),
        authorization.getPrincipalName(),
        toCsv(authorization.getAuthorizedScopes()),
        accessToken,
        refreshToken,
        authorization.getAttribute(Principal.class.getName()),
        authorization.getAttribute(OAuth2AuthorizationRequest.class.getName()),
        authorizationCode,
        authorization.getAttribute(OAuth2ParameterNames.STATE)
        );
    }

    public static OAuth2TokenExchangeGrantAuthorization convertOAuth2TokenExchangeGrantAuthorization(
    OAuth2Authorization authorization
    ) {
        AbstractGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        return new OAuth2TokenExchangeGrantAuthorization(
                authorization.getId(),
        authorization.getRegisteredClientId(),
        authorization.getPrincipalName(),
        toCsv(authorization.getAuthorizedScopes()),
        accessToken
        );
    }

    public static OAuth2DeviceCodeGrantAuthorization convertOAuth2DeviceCodeGrantAuthorization(
    OAuth2Authorization authorization
    ) {
        AbstractGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        AbstractGrantAuthorization.RefreshToken refreshToken = extractRefreshToken(authorization);
        OAuth2DeviceCodeGrantAuthorization.DeviceCode deviceCode = extractDeviceCode(authorization);
        OAuth2DeviceCodeGrantAuthorization.UserCode userCode = extractUserCode(authorization);

        Set<String> scopeSet = authorization.getAttribute(OAuth2ParameterNames.SCOPE);
        String requestedScopesCsv = toCsv(scopeSet);

        return new OAuth2DeviceCodeGrantAuthorization(
                authorization.getId(),
        authorization.getRegisteredClientId(),
        authorization.getPrincipalName(),
        toCsv(authorization.getAuthorizedScopes()),
        accessToken,
        refreshToken,
        authorization.getAttribute(Principal.class.getName()),
        deviceCode,
        userCode,
        requestedScopesCsv,
        authorization.getAttribute(OAuth2ParameterNames.STATE)
        );
    }

    public static OAuth2ClientCredentialsGrantAuthorization convertOAuth2ClientCredentialsGrantAuthorization(
    OAuth2Authorization authorization
    ) {
        AbstractGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        return new OAuth2ClientCredentialsGrantAuthorization(
                authorization.getId(),
        authorization.getRegisteredClientId(),
        authorization.getPrincipalName(),
        toCsv(authorization.getAuthorizedScopes()),
        accessToken
        );
    }

    public static OidcAuthorizationCodeGrantAuthorization convertOidcAuthorizationCodeGrantAuthorization(
    OAuth2Authorization authorization
    ) {
        OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode = extractAuthorizationCode(authorization);
        AbstractGrantAuthorization.AccessToken accessToken = extractAccessToken(authorization);
        AbstractGrantAuthorization.RefreshToken refreshToken = extractRefreshToken(authorization);
        OidcAuthorizationCodeGrantAuthorization.IdToken idToken = extractIdToken(authorization);

        return new OidcAuthorizationCodeGrantAuthorization(
                authorization.getId(),
        authorization.getRegisteredClientId(),
        authorization.getPrincipalName(),
        toCsv(authorization.getAuthorizedScopes()),
        accessToken,
        refreshToken,
		authorization.getAttribute(Principal.class.getName()),
        authorization.getAttribute(OAuth2AuthorizationRequest.class.getName()),
        authorizationCode,
        authorization.getAttribute(OAuth2ParameterNames.STATE),
        idToken
        );
    }

    public static OAuth2DeviceCodeGrantAuthorization.DeviceCode extractDeviceCode(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2DeviceCode> token = authorization.getToken(OAuth2DeviceCode.class);
        if (token == null) return null;
        OAuth2DeviceCode oauth2DeviceCode = token.getToken();
        return new OAuth2DeviceCodeGrantAuthorization.DeviceCode(
                oauth2DeviceCode.getTokenValue(),
        oauth2DeviceCode.getIssuedAt(),
        oauth2DeviceCode.getExpiresAt(),
        token.isInvalidated()
        );
    }

    public static OAuth2DeviceCodeGrantAuthorization.UserCode extractUserCode(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2UserCode> token = authorization.getToken(OAuth2UserCode.class);
        if (token == null) return null;
        OAuth2UserCode oauth2UserCode = token.getToken();
        return new OAuth2DeviceCodeGrantAuthorization.UserCode(
                oauth2UserCode.getTokenValue(),
        oauth2UserCode.getIssuedAt(),
        oauth2UserCode.getExpiresAt(),
        token.isInvalidated()
        );
    }

    public static AbstractGrantAuthorization.RefreshToken extractRefreshToken(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2RefreshToken> token = authorization.getRefreshToken();
        if (token == null) return null;
        OAuth2RefreshToken rt = token.getToken();
        return new AbstractGrantAuthorization.RefreshToken(
                rt.getTokenValue(),
        rt.getIssuedAt(),
        rt.getExpiresAt(),
        token.isInvalidated()
        );
    }

    public static OidcAuthorizationCodeGrantAuthorization.IdToken extractIdToken(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OidcIdToken> token = authorization.getToken(OidcIdToken.class);
        if (token == null) return null;
        OidcIdToken oidcIdToken = token.getToken();
        return new OidcAuthorizationCodeGrantAuthorization.IdToken(
                oidcIdToken.getTokenValue(),
        oidcIdToken.getIssuedAt(),
        oidcIdToken.getExpiresAt(),
        token.isInvalidated(),
        new AbstractGrantAuthorization.ClaimsHolder(oidcIdToken.getClaims())
        );
    }

    public static Map<String,Object> toMultiValueMapOfStrings(Map<String, Object> map) {
        LinkedMultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();
            if (value instanceof Collection<?>) {
                for (Object item : (Collection<?>) value) {
                    if (item != null) {
                        multiValueMap.add(key, item.toString());
                    }
                }
            } else if (value != null && value.getClass().isArray()) {
                Object[] arr = (Object[]) value;
                for (Object item : arr) {
                    if (item != null) {
                        multiValueMap.add(key, item.toString());
                    }
                }
            }  else {
				assert value != null;
				multiValueMap.add(key, value.toString());
            }
        }
        return multiValueMap.toSingleValueMap();
    }

    public static AbstractGrantAuthorization.AccessToken extractAccessToken(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AccessToken> token = authorization.getAccessToken();
        if (token == null) return null;

        OAuth2AccessToken at = token.getToken();
        OAuth2TokenFormat tokenFormat = null;

        Object fmt = token.getMetadata().get(OAuth2TokenFormat.class.getName());
        if (OAuth2TokenFormat.SELF_CONTAINED.getValue().equals(fmt)) {
            tokenFormat = OAuth2TokenFormat.SELF_CONTAINED;
        } else if (OAuth2TokenFormat.REFERENCE.getValue().equals(fmt)) {
            tokenFormat = OAuth2TokenFormat.REFERENCE;
        }

		Map<String, Object> map = null;
		if (authorization.getAccessToken() != null && authorization.getAccessToken().getClaims() != null) {
			map = toMultiValueMapOfStrings(authorization.getAccessToken().getClaims());
		}
        return new AbstractGrantAuthorization.AccessToken(
                at.getTokenValue(),
        at.getIssuedAt(),
        at.getExpiresAt(),
        token.isInvalidated(),
        at.getTokenType(),
        toCsv(at.getScopes()),
        tokenFormat,
        new AbstractGrantAuthorization.ClaimsHolder(map));
    }

    public static OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode extractAuthorizationCode(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> token = authorization.getToken(OAuth2AuthorizationCode.class);
        if (token == null) return null;

        OAuth2AuthorizationCode ac = token.getToken();
        return new OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode(
                ac.getTokenValue(),
        ac.getIssuedAt(),
        ac.getExpiresAt(),
        token.isInvalidated()
        );
    }
}
