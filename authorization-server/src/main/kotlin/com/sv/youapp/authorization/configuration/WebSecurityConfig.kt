package com.sv.youapp.authorization.configuration

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.sv.youapp.authorization.converter.NativeAuthenticationConverter
import com.sv.youapp.authorization.providers.NativeAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.UUID

@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(
        http: HttpSecurity,
        authorizationService: OAuth2AuthorizationService,
        tokenGenerator: OAuth2TokenGenerator<OAuth2Token>,
    ): SecurityFilterChain {
        val authorizationServerConfigurer: OAuth2AuthorizationServerConfigurer =
            OAuth2AuthorizationServerConfigurer.authorizationServer()
        http
            .securityMatcher(authorizationServerConfigurer.endpointsMatcher)
            .with(authorizationServerConfigurer) { config: OAuth2AuthorizationServerConfigurer ->
                config.tokenEndpoint { configurer: OAuth2TokenEndpointConfigurer ->
                    configurer.accessTokenRequestConverter(NativeAuthenticationConverter())
                    configurer.authenticationProvider(
                        NativeAuthenticationProvider(authorizationService, tokenGenerator),
                    )
                }
                config.oidc(Customizer.withDefaults())
            }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }.csrf { csrf -> csrf.disable() }
//            .exceptionHandling { exceptions ->
//                exceptions.defaultAuthenticationEntryPointFor(
//                    LoginUrlAuthenticationEntryPoint("/login"),
//                    MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                )
//            }
        return http.build()
    }

    @Bean
    fun authorizationService(): OAuth2AuthorizationService {
        return InMemoryOAuth2AuthorizationService()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
            .formLogin(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val user =
            User.withDefaultPasswordEncoder()
                .username("user")
                .password("{noop}password")
                .roles("USER")
                .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val oidcClient =
            RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType("urn:ietf:params:oauth:grant-type:native"))
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
                // .postLogoutRedirectUri("http://127.0.0.1:8080/")
                // .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(
                    ClientSettings.builder().requireProofKey(true)
                        .requireAuthorizationConsent(false).build(),
                )
                .build()

        return InMemoryRegisteredClientRepository(oidcClient)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val rsaKey =
            RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }

    @Bean
    fun tokenGenerator(jwkSource: JWKSource<SecurityContext>): OAuth2TokenGenerator<OAuth2Token> {
        val jwtEncoder = NimbusJwtEncoder(jwkSource)
        val jwtGenerator = JwtGenerator(jwtEncoder)
        return DelegatingOAuth2TokenGenerator(
            OAuth2AccessTokenGenerator(),
            OAuth2RefreshTokenGenerator(),
            jwtGenerator,
        )
    }

    private fun generateRsaKey(): KeyPair {
        return try {
            val kpg = KeyPairGenerator.getInstance("RSA")
            kpg.initialize(2048)
            kpg.generateKeyPair()
        } catch (ex: Exception) {
            throw IllegalStateException(ex)
        }
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder =
        OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings = AuthorizationServerSettings.builder().build()
}
