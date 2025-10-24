package com.sv.youapp.bff.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import com.sv.youapp.bff.services.TokenExchangeService;
import com.sv.youapp.bff.services.impl.DefaultTokenExchangeService;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(ServerHttpSecurity.CorsSpec::disable)
            .authorizeExchange(ex -> ex.anyExchange().permitAll())
        .build();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
            .baseUrl("http://localhost:8082")
            .build();
    }

    @Bean
    public TokenExchangeService tokenExchangeService(WebClient webClient) {
        return new DefaultTokenExchangeService(webClient);
    }
}
