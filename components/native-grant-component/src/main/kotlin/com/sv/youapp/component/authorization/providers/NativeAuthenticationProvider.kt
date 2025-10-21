package com.sv.youapp.component.authorization.providers

import com.sv.youapp.common.authorization.authentication.NATIVE_GRANT_TYPE
import com.sv.youapp.common.authorization.authentication.NativeAuthentication
import com.sv.youapp.common.authorization.services.NativeAuthenticationService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClaimAccessor
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator
import java.util.Collections

class NativeAuthenticationProvider(
    private val authorizationService: OAuth2AuthorizationService,
    private val tokenGenerator: OAuth2TokenGenerator<out OAuth2Token>,
    private val nativeAuthenticationService: NativeAuthenticationService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        val nativeAuthentication: NativeAuthentication =
            authentication as NativeAuthentication

        val clientPrincipal = nativeAuthentication.clientPrincipal as OAuth2ClientAuthenticationToken
        val registeredClient: RegisteredClient = clientPrincipal.registeredClient as RegisteredClient

        if (!registeredClient.authorizationGrantTypes.contains(NATIVE_GRANT_TYPE)) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT)
        }
        // DO NECESSARY STUFF, GET FROM DB, GET ROLES, AND PUT IN
        val user: UserDetails = nativeAuthenticationService.authenticate(authentication)
        nativeAuthentication.granted = user.authorities.toSet()
        //
        nativeAuthentication.isAuthenticated = true
        val tokenContextBuilder: DefaultOAuth2TokenContext.Builder =
            DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(nativeAuthentication)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(nativeAuthentication.scopes.map { it.toString() }.toSet())
                .authorizationGrantType(NATIVE_GRANT_TYPE)
                .authorizationGrant(nativeAuthentication)

        // ----- Access token -----
        var tokenContext: OAuth2TokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build()

        val generatedAccessToken: OAuth2Token =
            requireNotNull(this.tokenGenerator.generate(tokenContext)) {
                throw OAuth2AuthenticationException(
                    OAuth2Error(
                        OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the access token.",
                        "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2",
                    ),
                )
            }
        val accessToken =
            OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.tokenValue,
                generatedAccessToken.issuedAt,
                generatedAccessToken.expiresAt,
                nativeAuthentication.scopes.map { it.toString() }.toSet(),
            )

        // ----- Refresh token -----
        var refreshToken: OAuth2RefreshToken? = null

        // Do not issue refresh token to public client
        if (registeredClient.authorizationGrantTypes.contains(AuthorizationGrantType.REFRESH_TOKEN)) {
            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build()
            val generatedRefreshToken = this.tokenGenerator.generate(tokenContext)
            if (generatedRefreshToken != null) {
                if (generatedRefreshToken !is OAuth2RefreshToken) {
                    val error =
                        OAuth2Error(
                            OAuth2ErrorCodes.SERVER_ERROR,
                            "The token generator failed to generate a valid refresh token.",
                            "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2",
                        )
                    throw OAuth2AuthenticationException(error)
                }
                refreshToken = generatedRefreshToken
            }
        }

        val authorizationBuilder =
            OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(nativeAuthentication.principal as String?)
                .authorizationGrantType(NATIVE_GRANT_TYPE)
        if (generatedAccessToken is ClaimAccessor) {
            authorizationBuilder.token(
                accessToken,
            ) { metadata: MutableMap<String?, Any?>? ->
                metadata!![OAuth2Authorization.Token.CLAIMS_METADATA_NAME] = generatedAccessToken.claims
            }
        } else {
            authorizationBuilder.accessToken(accessToken)
        }

        val authorization = authorizationBuilder.build()
        this.authorizationService.save(authorization)
        return OAuth2AccessTokenAuthenticationToken(
            registeredClient,
            clientPrincipal,
            accessToken,
            refreshToken,
            Collections.emptyMap(),
        )
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return if (authentication == null) false else NativeAuthentication::class.java.isAssignableFrom(authentication)
    }
}
