package com.sv.youapp.authorization.configuration

import com.sv.youapp.authorization.converter.NativeAuthenticationConverter
import com.sv.youapp.authorization.providers.NativeAuthenticationProvider
import com.sv.youapp.authorization.services.AuthenticationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher

@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(
        http: HttpSecurity,
        authorizationService: OAuth2AuthorizationService,
        tokenGenerator: OAuth2TokenGenerator<OAuth2Token>,
        authenticationService: AuthenticationService,
    ): SecurityFilterChain {
        val authorizationServerConfigurer: OAuth2AuthorizationServerConfigurer =
            OAuth2AuthorizationServerConfigurer.authorizationServer()
        http
            .securityMatcher(authorizationServerConfigurer.endpointsMatcher)
            .with(authorizationServerConfigurer) { config: OAuth2AuthorizationServerConfigurer ->
                config.tokenEndpoint { configurer: OAuth2TokenEndpointConfigurer ->
                    configurer.accessTokenRequestConverter(NativeAuthenticationConverter())
                    configurer.authenticationProvider(
                        NativeAuthenticationProvider(authorizationService, tokenGenerator, authenticationService),
                    )
                }
                config.oidc(Customizer.withDefaults())
            }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }.csrf { csrf -> csrf.disable() }
            .exceptionHandling { exceptions ->
                exceptions.defaultAuthenticationEntryPointFor(
                    LoginUrlAuthenticationEntryPoint("/login"),
                    MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            }
        return http.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
            .formLogin(Customizer.withDefaults())
        return http.build()
    }
}
