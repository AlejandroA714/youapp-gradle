package com.sv.youapp.service.authorization.configuration

import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.provisioning.InMemoryUserDetailsManager

private const val AUTHORITIES = "authorities"

@Configuration
class WebSecurityConfig {

    @Bean
    fun userDetailsService(props: SecurityProperties): UserDetailsService {
        val u = props.user
        val user = User.withUsername(u.name)
            .password(u.password)
            .roles(*u.roles.toTypedArray())
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun jwtTokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext?> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext? ->
            if (OAuth2TokenType.ACCESS_TOKEN == context!!.tokenType) {
                val principal: Authentication = context.getPrincipal()
                val authorities =
                    principal.authorities
                        ?.map { it.authority }
                        ?.toList()
                        ?: emptyList()
                context.claims.claims { claims ->
                    claims[AUTHORITIES] = authorities
                }
            }
        }
    }


//    @Bean
//    @Order(1)
//    fun authorizationServerSecurityFilterChain(
//        http: HttpSecurity,
//       // authorizationService: OAuth2AuthorizationService,
//        tokenGenerator: OAuth2TokenGenerator<OAuth2Token>,
//       // authenticationService: AuthenticationService,
//    ): SecurityFilterChain {
//        val authorizationServerConfigurer: OAuth2AuthorizationServerConfigurer =
//            OAuth2AuthorizationServerConfigurer.authorizationServer()
//        http
//            .securityMatcher(authorizationServerConfigurer.endpointsMatcher)
//            .with(authorizationServerConfigurer) { config: OAuth2AuthorizationServerConfigurer ->
//                config.tokenEndpoint { configurer: OAuth2TokenEndpointConfigurer ->
//                   // configurer.accessTokenRequestConverter(NativeAuthenticationConverter())
//                  //  configurer.authenticationProvider(
//                   //     NativeAuthenticationProvider(authorizationService, tokenGenerator, authenticationService),
//                   // )
//                }
//                config.oidc(Customizer.withDefaults())
//            }
//            .authorizeHttpRequests { auth ->
//                auth.anyRequest().authenticated()
//            }.csrf { csrf -> csrf.disable() }
//            .exceptionHandling { exceptions ->
//                exceptions.defaultAuthenticationEntryPointFor(
//                    LoginUrlAuthenticationEntryPoint("/login"),
//                    MediaTypeRequestMatcher(MediaType.TEXT_HTML),
//                )
//            }
//        return http.build()
//    }
//
//    @Bean
//    @Order(2)
//    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http
//            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
//            .csrf { csrf -> csrf.disable() }
//            .formLogin(Customizer.withDefaults())
//        return http.build()
//    }
}
