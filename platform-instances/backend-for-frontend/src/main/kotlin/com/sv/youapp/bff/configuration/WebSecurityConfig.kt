package com.sv.youapp.bff.configuration

import com.sv.youapp.bff.services.TokenExchangeService
import com.sv.youapp.bff.services.impl.DefaultTokenExchangeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .cors { it.disable() }
            .authorizeExchange{ ex -> ex.anyExchange().permitAll()}.build()

    }

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder
            .baseUrl("http://localhost:8082")
            //.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @Bean
    fun tokenExchangeService(webClient: WebClient): TokenExchangeService {
        return DefaultTokenExchangeService(webClient)
    }

}
