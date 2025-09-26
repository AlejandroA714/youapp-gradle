package com.sv.youapp.authorization.mapper

import com.sv.youapp.authorization.entities.redis.AbstractGrantAuthorization
import com.sv.youapp.authorization.entities.redis.AbstractGrantAuthorization.AccessToken
import com.sv.youapp.authorization.entities.redis.AbstractGrantAuthorization.ClaimsHolder
import com.sv.youapp.authorization.entities.redis.AbstractGrantAuthorization.RefreshToken
import com.sv.youapp.authorization.entities.redis.OAuth2AuthorizationCodeGrantAuthorization
import com.sv.youapp.authorization.entities.redis.OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode
import com.sv.youapp.authorization.entities.redis.OAuth2ClientCredentialsGrantAuthorization
import com.sv.youapp.authorization.entities.redis.OAuth2DeviceCodeGrantAuthorization
import com.sv.youapp.authorization.entities.redis.OAuth2DeviceCodeGrantAuthorization.DeviceCode
import com.sv.youapp.authorization.entities.redis.OAuth2DeviceCodeGrantAuthorization.UserCode
import com.sv.youapp.authorization.entities.redis.OAuth2TokenExchangeGrantAuthorization
import com.sv.youapp.authorization.entities.redis.OidcAuthorizationCodeGrantAuthorization
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2DeviceCode
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.OAuth2UserCode
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils
import java.security.Principal

fun convertOAuth2AuthorizationGrantAuthorization(authorization: OAuth2Authorization): AbstractGrantAuthorization? {
    if (AuthorizationGrantType.AUTHORIZATION_CODE == authorization.authorizationGrantType) {
        val authorizationRequest =
            authorization
                .getAttribute<OAuth2AuthorizationRequest?>(OAuth2AuthorizationRequest::class.java.getName())
        return if (authorizationRequest!!.scopes.contains(OidcScopes.OPENID)) {
            convertOidcAuthorizationCodeGrantAuthorization(authorization)
        } else {
            convertOAuth2AuthorizationCodeGrantAuthorization(authorization)
        }
    } else if (AuthorizationGrantType.CLIENT_CREDENTIALS == authorization.authorizationGrantType) {
        return convertOAuth2ClientCredentialsGrantAuthorization(authorization)
    } else if (AuthorizationGrantType.DEVICE_CODE == authorization.authorizationGrantType) {
        return convertOAuth2DeviceCodeGrantAuthorization(authorization)
    } else if (AuthorizationGrantType.TOKEN_EXCHANGE == authorization.authorizationGrantType) {
        return convertOAuth2TokenExchangeGrantAuthorization(authorization)
    }
    // TODO: NATIVE NO SUPPORTED
    return null
}

fun mapOAuth2AuthorizationGrantAuthorization(
    authorizationGrantAuthorization: AbstractGrantAuthorization?,
    builder: OAuth2Authorization.Builder,
) {
    when (authorizationGrantAuthorization) {
        is OidcAuthorizationCodeGrantAuthorization -> {
            mapOidcAuthorizationCodeGrantAuthorization(authorizationGrantAuthorization, builder)
        }

        is OAuth2AuthorizationCodeGrantAuthorization -> {
            mapOAuth2AuthorizationCodeGrantAuthorization(authorizationGrantAuthorization, builder)
        }

        is OAuth2ClientCredentialsGrantAuthorization -> {
            mapOAuth2ClientCredentialsGrantAuthorization(authorizationGrantAuthorization, builder)
        }

        is OAuth2DeviceCodeGrantAuthorization -> {
            mapOAuth2DeviceCodeGrantAuthorization(authorizationGrantAuthorization, builder)
        }

        is OAuth2TokenExchangeGrantAuthorization -> {
            mapOAuth2TokenExchangeGrantAuthorization(authorizationGrantAuthorization, builder)
        }
    }
}

fun mapOAuth2TokenExchangeGrantAuthorization(
    tokenExchangeGrantAuthorization: OAuth2TokenExchangeGrantAuthorization,
    builder: OAuth2Authorization.Builder,
) {
    builder.id(tokenExchangeGrantAuthorization.id)
        .principalName(tokenExchangeGrantAuthorization.principalName)
        .authorizationGrantType(AuthorizationGrantType.TOKEN_EXCHANGE)
        .authorizedScopes(tokenExchangeGrantAuthorization.scopes.toStringSet())

    mapAccessToken(tokenExchangeGrantAuthorization.accessToken, builder)
}

fun Set<String>?.toCsv(): String = this?.joinToString(",") ?: ""

fun String?.toStringSet(): Set<String> =
    if (this.isNullOrBlank()) {
        emptySet()
    } else {
        this.split(",").map { it.trim() }.toSet()
    }

fun mapOAuth2DeviceCodeGrantAuthorization(
    deviceCodeGrantAuthorization: OAuth2DeviceCodeGrantAuthorization,
    builder: OAuth2Authorization.Builder,
) {
    builder.id(deviceCodeGrantAuthorization.id)
        .principalName(deviceCodeGrantAuthorization.principalName)
        .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
        .authorizedScopes(deviceCodeGrantAuthorization.scopes.toStringSet())
    deviceCodeGrantAuthorization.principal?.let {
        builder.attribute(Principal::class.java.getName(), deviceCodeGrantAuthorization.principal)
    }

    builder.attribute(OAuth2ParameterNames.SCOPE, deviceCodeGrantAuthorization.requestedScopes.toStringSet())

    deviceCodeGrantAuthorization.deviceState?.let {
        builder.attribute(OAuth2ParameterNames.STATE, deviceCodeGrantAuthorization.deviceState)
    }
    mapAccessToken(deviceCodeGrantAuthorization.accessToken, builder)
    mapRefreshToken(deviceCodeGrantAuthorization.refreshToken, builder)
    mapDeviceCode(deviceCodeGrantAuthorization.deviceCode, builder)
    mapUserCode(deviceCodeGrantAuthorization.userCode, builder)
}

fun mapUserCode(
    userCode: UserCode?,
    builder: OAuth2Authorization.Builder,
) {
    if (userCode == null) {
        return
    }
    val oauth2UserCode =
        OAuth2UserCode(
            userCode.tokenValue,
            userCode.issuedAt,
            userCode.expiresAt,
        )
    builder.token(oauth2UserCode) { metadata: MutableMap<String?, Any?>? ->
        metadata!![OAuth2Authorization.Token.INVALIDATED_METADATA_NAME] = userCode.invalidated
    }
}

fun mapDeviceCode(
    deviceCode: DeviceCode?,
    builder: OAuth2Authorization.Builder,
) {
    if (deviceCode == null) {
        return
    }
    val oauth2DeviceCode =
        OAuth2DeviceCode(
            deviceCode.tokenValue,
            deviceCode.issuedAt,
            deviceCode.expiresAt,
        )
    builder.token(oauth2DeviceCode) { metadata: MutableMap<String?, Any?>? ->
        metadata!![OAuth2Authorization.Token.INVALIDATED_METADATA_NAME] = deviceCode.invalidated
    }
}

fun mapOAuth2ClientCredentialsGrantAuthorization(
    clientCredentialsGrantAuthorization: OAuth2ClientCredentialsGrantAuthorization,
    builder: OAuth2Authorization.Builder,
) {
    builder.id(clientCredentialsGrantAuthorization.id)
        .principalName(clientCredentialsGrantAuthorization.principalName)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .authorizedScopes(clientCredentialsGrantAuthorization.scopes.toStringSet())

    mapAccessToken(clientCredentialsGrantAuthorization.accessToken, builder)
}

fun mapOidcAuthorizationCodeGrantAuthorization(
    authorizationCodeGrantAuthorization: OidcAuthorizationCodeGrantAuthorization,
    builder: OAuth2Authorization.Builder,
) {
    mapOAuth2AuthorizationCodeGrantAuthorization(authorizationCodeGrantAuthorization, builder)
    mapIdToken(authorizationCodeGrantAuthorization.idToken, builder)
}

fun mapIdToken(
    idToken: OidcAuthorizationCodeGrantAuthorization.IdToken?,
    builder: OAuth2Authorization.Builder,
) {
    if (idToken == null) {
        return
    }
    val oidcIdToken =
        OidcIdToken(
            idToken.tokenValue,
            idToken.issuedAt,
            idToken.expiresAt,
            idToken.claims?.claims,
        )
    builder.token(oidcIdToken) { metadata: MutableMap<String?, Any?>? ->
        metadata!![OAuth2Authorization.Token.INVALIDATED_METADATA_NAME] = idToken.invalidated
        metadata[OAuth2Authorization.Token.CLAIMS_METADATA_NAME] = idToken.claims?.claims
    }
}

fun mapOAuth2AuthorizationCodeGrantAuthorization(
    authorizationCodeGrantAuthorization: OAuth2AuthorizationCodeGrantAuthorization,
    builder: OAuth2Authorization.Builder,
) {
    val req: OAuth2AuthorizationRequest? = authorizationCodeGrantAuthorization.authorizationRequest
    val safeReq =
        OAuth2AuthorizationRequest.from(req)
            .attributes(req?.attributes ?: emptyMap())
            .additionalParameters(req?.additionalParameters ?: emptyMap())
            .build()
    builder.id(authorizationCodeGrantAuthorization.id)
        .principalName(authorizationCodeGrantAuthorization.principalName)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizedScopes(authorizationCodeGrantAuthorization.scopes.toStringSet())
        .attribute(Principal::class.java.getName(), authorizationCodeGrantAuthorization.principal)
        .attribute(
            OAuth2AuthorizationRequest::class.java.getName(),
            safeReq,
        )
    if (StringUtils.hasText(authorizationCodeGrantAuthorization.state)) {
        builder.attribute(OAuth2ParameterNames.STATE, authorizationCodeGrantAuthorization.state)
    }

    mapAuthorizationCode(authorizationCodeGrantAuthorization.authorizationCode, builder)
    mapAccessToken(authorizationCodeGrantAuthorization.accessToken, builder)
    mapRefreshToken(authorizationCodeGrantAuthorization.refreshToken, builder)
}

fun mapRefreshToken(
    refreshToken: RefreshToken?,
    builder: OAuth2Authorization.Builder,
) {
    if (refreshToken == null) {
        return
    }
    val oauth2RefreshToken =
        OAuth2RefreshToken(
            refreshToken.tokenValue,
            refreshToken.issuedAt,
            refreshToken.expiresAt,
        )
    builder.token(oauth2RefreshToken) { metadata: MutableMap<String?, Any?>? ->
        metadata!![OAuth2Authorization.Token.INVALIDATED_METADATA_NAME] = refreshToken.invalidated
    }
}

fun mapAuthorizationCode(
    authorizationCode: AuthorizationCode?,
    builder: OAuth2Authorization.Builder,
) {
    if (authorizationCode == null) {
        return
    }
    val oauth2AuthorizationCode =
        OAuth2AuthorizationCode(
            authorizationCode.tokenValue,
            authorizationCode.issuedAt,
            authorizationCode.expiresAt,
        )
    builder.token(oauth2AuthorizationCode) { metadata: MutableMap<String?, Any?>? ->
        metadata!![OAuth2Authorization.Token.INVALIDATED_METADATA_NAME] = authorizationCode.invalidated
    }
}

fun mapAccessToken(
    accessToken: AccessToken?,
    builder: OAuth2Authorization.Builder,
) {
    if (accessToken == null) {
        return
    }
    val oauth2AccessToken =
        OAuth2AccessToken(
            accessToken.tokenType,
            accessToken.tokenValue,
            accessToken.issuedAt,
            accessToken.expiresAt,
            accessToken.scopes.toStringSet(),
        )
    builder.token(oauth2AccessToken) { metadata: MutableMap<String?, Any?>? ->
        metadata!![OAuth2Authorization.Token.INVALIDATED_METADATA_NAME] = accessToken.invalidated
        metadata[OAuth2Authorization.Token.CLAIMS_METADATA_NAME] = accessToken.claims?.claims
        metadata[OAuth2TokenFormat::class.java.getName()] = accessToken.tokenFormat?.value
    }
}

fun convertOAuth2AuthorizationCodeGrantAuthorization(authorization: OAuth2Authorization): OAuth2AuthorizationCodeGrantAuthorization {
    val authorizationCode = extractAuthorizationCode(authorization)
    val refreshToken: RefreshToken? = extractRefreshToken(authorization)
    val accessToken: AccessToken? = extractAccessToken(authorization)
    return OAuth2AuthorizationCodeGrantAuthorization(
        authorization.id,
        authorization.registeredClientId, authorization.principalName,
        authorization.authorizedScopes.toCsv(), accessToken, refreshToken,
        authorization.getAttribute<Principal?>(Principal::class.java.getName()),
        authorization.getAttribute<OAuth2AuthorizationRequest?>(OAuth2AuthorizationRequest::class.java.getName()), authorizationCode,
        authorization.getAttribute<String?>(OAuth2ParameterNames.STATE),
    )
}

fun convertOAuth2TokenExchangeGrantAuthorization(authorization: OAuth2Authorization): OAuth2TokenExchangeGrantAuthorization {
    val accessToken: AccessToken? = extractAccessToken(authorization)

    return OAuth2TokenExchangeGrantAuthorization(
        authorization.id,
        authorization.registeredClientId,
        authorization.principalName,
        authorization.authorizedScopes.toCsv(),
        accessToken,
    )
}

fun convertOAuth2DeviceCodeGrantAuthorization(authorization: OAuth2Authorization): OAuth2DeviceCodeGrantAuthorization {
    val accessToken: AccessToken? = extractAccessToken(authorization)
    val refreshToken = extractRefreshToken(authorization)
    val deviceCode: DeviceCode? = extractDeviceCode(authorization)
    val userCode: UserCode? = extractUserCode(authorization)

    return OAuth2DeviceCodeGrantAuthorization(
        authorization.id, authorization.registeredClientId,
        authorization.principalName, authorization.authorizedScopes.toCsv(), accessToken, refreshToken,
        authorization.getAttribute<Principal?>(Principal::class.java.getName()), deviceCode, userCode,
        (authorization.getAttribute<Set<String>?>(OAuth2ParameterNames.SCOPE)?.toCsv() as String),
        authorization.getAttribute<String?>(OAuth2ParameterNames.STATE),
    )
}

fun convertOAuth2ClientCredentialsGrantAuthorization(authorization: OAuth2Authorization): OAuth2ClientCredentialsGrantAuthorization {
    val accessToken = extractAccessToken(authorization)

    return OAuth2ClientCredentialsGrantAuthorization(
        authorization.id,
        authorization.registeredClientId,
        authorization.principalName,
        authorization.authorizedScopes.toCsv(),
        accessToken,
    )
}

fun convertOidcAuthorizationCodeGrantAuthorization(authorization: OAuth2Authorization): OidcAuthorizationCodeGrantAuthorization {
    val authorizationCode: AuthorizationCode? =
        extractAuthorizationCode(
            authorization,
        )
    val accessToken: AccessToken? = extractAccessToken(authorization)
    val refreshToken: RefreshToken? = extractRefreshToken(authorization)
    val idToken: OidcAuthorizationCodeGrantAuthorization.IdToken? = extractIdToken(authorization)
    return OidcAuthorizationCodeGrantAuthorization(
        authorization.id, authorization.registeredClientId,
        authorization.principalName, authorization.authorizedScopes.toCsv(), accessToken, refreshToken,
        authorization.getAttribute<Any?>(Principal::class.java.getName()) as Principal?,
        authorization.getAttribute<Any?>(OAuth2AuthorizationRequest::class.java.getName()) as OAuth2AuthorizationRequest?, authorizationCode,
        authorization.getAttribute<Any?>(OAuth2ParameterNames.STATE) as String?, idToken,
    )
}

fun extractDeviceCode(authorization: OAuth2Authorization): DeviceCode? {
    var deviceCode: DeviceCode? = null
    if (authorization.getToken(OAuth2DeviceCode::class.java) != null) {
        val oauth2DeviceCode =
            authorization
                .getToken(OAuth2DeviceCode::class.java)
        deviceCode =
            DeviceCode(
                oauth2DeviceCode!!.getToken()!!.tokenValue,
                oauth2DeviceCode.getToken()!!.issuedAt,
                oauth2DeviceCode.getToken()!!.expiresAt,
                oauth2DeviceCode.isInvalidated,
            )
    }
    return deviceCode
}

fun extractUserCode(authorization: OAuth2Authorization): UserCode? {
    var userCode: UserCode? = null
    if (authorization.getToken(OAuth2UserCode::class.java) != null) {
        val oauth2UserCode = authorization.getToken(OAuth2UserCode::class.java)
        userCode =
            UserCode(
                oauth2UserCode!!.getToken()!!.tokenValue,
                oauth2UserCode.getToken()!!.issuedAt,
                oauth2UserCode.getToken()!!.expiresAt,
                oauth2UserCode.isInvalidated,
            )
    }
    return userCode
}

fun extractRefreshToken(authorization: OAuth2Authorization): RefreshToken? {
    var refreshToken: RefreshToken? = null
    if (authorization.refreshToken != null) {
        val oauth2RefreshToken = authorization.refreshToken
        refreshToken =
            RefreshToken(
                oauth2RefreshToken!!.getToken()!!.tokenValue,
                oauth2RefreshToken.getToken()!!.issuedAt,
                oauth2RefreshToken.getToken()!!.expiresAt,
                oauth2RefreshToken.isInvalidated,
            )
    }
    return refreshToken
}

fun extractIdToken(authorization: OAuth2Authorization): OidcAuthorizationCodeGrantAuthorization.IdToken? {
    var idToken: OidcAuthorizationCodeGrantAuthorization.IdToken? = null
    if (authorization.getToken(OidcIdToken::class.java) != null) {
        val oidcIdToken = authorization.getToken(OidcIdToken::class.java)
        idToken =
            OidcAuthorizationCodeGrantAuthorization.IdToken(
                oidcIdToken!!.getToken()!!.tokenValue,
                oidcIdToken.getToken()!!.issuedAt,
                oidcIdToken.getToken()!!.expiresAt,
                oidcIdToken.isInvalidated,
                ClaimsHolder(oidcIdToken.claims),
            )
    }
    return idToken
}

fun Map<String, Any?>.toMultiValueMapOfStrings(): MultiValueMap<String, String> {
    val multiValueMap = LinkedMultiValueMap<String, String>()
    for ((key, value) in this) {
        when (value) {
            is Collection<*> -> {
                value.forEach { item ->
                    item?.toString()?.let { multiValueMap.add(key, it) }
                }
            }
            is Array<*> -> {
                value.forEach { item ->
                    item?.toString()?.let { multiValueMap.add(key, it) }
                }
            }
            null -> {}
            else -> {
                multiValueMap.add(key, value.toString())
            }
        }
    }
    return multiValueMap
}

fun extractAccessToken(authorization: OAuth2Authorization): AccessToken? {
    var accessToken: AccessToken? = null
    if (authorization.accessToken != null) {
        val oauth2AccessToken = authorization.accessToken
        var tokenFormat: OAuth2TokenFormat? = null
        if (OAuth2TokenFormat.SELF_CONTAINED.value
            == oauth2AccessToken.getMetadata<Any?>(OAuth2TokenFormat::class.java.getName())
        ) {
            tokenFormat = OAuth2TokenFormat.SELF_CONTAINED
        } else if (OAuth2TokenFormat.REFERENCE.value
            == oauth2AccessToken.getMetadata<Any?>(OAuth2TokenFormat::class.java.getName())
        ) {
            tokenFormat = OAuth2TokenFormat.REFERENCE
        }
        accessToken =
            AccessToken(
                oauth2AccessToken.getToken()!!.tokenValue,
                oauth2AccessToken.getToken()!!.issuedAt!!,
                oauth2AccessToken.getToken()!!.expiresAt!!,
                oauth2AccessToken.isInvalidated,
                oauth2AccessToken.getToken()!!.tokenType,
                oauth2AccessToken.getToken()!!.scopes.toCsv(),
                tokenFormat,
                ClaimsHolder(oauth2AccessToken.claims?.toMultiValueMapOfStrings()),
            )
    }
    return accessToken
}

fun extractAuthorizationCode(authorization: OAuth2Authorization): AuthorizationCode? {
    var authorizationCode: AuthorizationCode? = null
    if (authorization.getToken(OAuth2AuthorizationCode::class.java) != null) {
        val oauth2AuthorizationCode =
            authorization
                .getToken(OAuth2AuthorizationCode::class.java)
        authorizationCode =
            AuthorizationCode(
                oauth2AuthorizationCode!!.getToken()!!.tokenValue,
                oauth2AuthorizationCode.getToken()!!.issuedAt,
                oauth2AuthorizationCode.getToken()!!.expiresAt,
                oauth2AuthorizationCode.isInvalidated,
            )
    }
    return authorizationCode
}
