package com.sv.youapp.service.authorization.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.web.SecurityFilterChain

@Configuration
class WebSecurityConfig {
    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(
        http: HttpSecurity,
        configurers: List<AbstractHttpConfigurer<*, HttpSecurity>>,
    ): SecurityFilterChain {
        val authorizationServerConfigurer: OAuth2AuthorizationServerConfigurer =
            OAuth2AuthorizationServerConfigurer.authorizationServer()
        http
            .securityMatcher(authorizationServerConfigurer.endpointsMatcher)
            .with(authorizationServerConfigurer) {}
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }.csrf { csrf -> csrf.disable() }
            .formLogin(Customizer.withDefaults())
        configurers.forEach {
            http.with(it) {}
        }
        return http.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth -> auth.anyRequest().authenticated() }
            .csrf { csrf -> csrf.disable() }
            .formLogin(Customizer.withDefaults())
        return http.build()
    }
}
