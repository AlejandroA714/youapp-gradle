package com.sv.youapp.authorization.providers

import com.sv.youapp.authorization.authentication.NATIVE_GRANT_TYPE
import com.sv.youapp.authorization.authentication.NativeAuthentication
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.ClaimAccessor
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator

class NativeAuthenticationProvider(
    private val authorizationService: OAuth2AuthorizationService,
    private val tokenGenerator: OAuth2TokenGenerator<OAuth2Token>,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        val nativeAuthentication: NativeAuthentication =
            authentication as NativeAuthentication

        val clientPrincipal = nativeAuthentication.clientPrincipal
        val registeredClient: RegisteredClient = clientPrincipal.registeredClient as RegisteredClient

        if (!registeredClient.authorizationGrantTypes.contains(NATIVE_GRANT_TYPE)) {
            throw OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        // DO NECESSARY STUFF, GET FROM DB, GET ROLES, AND PUT IN

        val tokenContext: OAuth2TokenContext =
            DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(nativeAuthentication)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizedScopes(nativeAuthentication.scopes.map { it.toString() }.toSet())
                .authorizationGrantType(NATIVE_GRANT_TYPE)
                .authorizationGrant(nativeAuthentication)
                .build()
        val generatedAccessToken: OAuth2Token =
            requireNotNull(this.tokenGenerator.generate(tokenContext)) {
                throw OAuth2AuthenticationException(
                    OAuth2Error(
                        OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the access token.",
                        null,
                    ),
                )
            }
        val accessToken =
            OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.tokenValue,
                generatedAccessToken.issuedAt,
                generatedAccessToken.expiresAt,
                null,
            )

        val authorizationBuilder =
            OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName(nativeAuthentication.principal as String?)
                .authorizationGrantType(NATIVE_GRANT_TYPE)
        if (generatedAccessToken is ClaimAccessor) {
            authorizationBuilder.token(
                accessToken)
            { metadata: MutableMap<String?, Any?>? ->
                metadata!![OAuth2Authorization.Token.CLAIMS_METADATA_NAME] = generatedAccessToken.claims
            }
        } else {
            authorizationBuilder.accessToken(accessToken)
        }
        val authorization = authorizationBuilder.build()
        this.authorizationService.save(authorization)
        return OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return if (authentication == null) false else NativeAuthentication::class.java.isAssignableFrom(authentication)
    }
}
