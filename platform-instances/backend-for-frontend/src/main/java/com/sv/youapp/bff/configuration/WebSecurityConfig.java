package com.sv.youapp.bff.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sv.youapp.bff.properties.BackEndForFrontEndProperties;
import com.sv.youapp.bff.services.TokenExchangeService;
import com.sv.youapp.bff.services.impl.DefaultTokenExchangeService;
import com.sv.youapp.bff.services.impl.InMemorySessionStorage;
import com.sv.youapp.common.authorization.dto.SessionRequest;
import com.sv.youapp.common.authorization.services.SessionStorage;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebSecurityConfig {
  @Bean
  SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(ServerHttpSecurity.CorsSpec::disable)
        .authorizeExchange(ex -> ex.anyExchange().permitAll())
        .build();
  }

  @Bean
  WebClient webClient(BackEndForFrontEndProperties properties) {
    return WebClient.builder().baseUrl(properties.url().toString()).build();
  }

  @Bean
  Cache<String, SessionRequest> caffeineCache() {
    return Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(5L)).build();
  }

  @Bean
  SessionStorage sessionStorage(Cache<String, SessionRequest> cache) {
    return new InMemorySessionStorage(cache);
  }

  @Bean
  TokenExchangeService tokenExchangeService(
      WebClient webClient, SessionStorage sessionStorage, BackEndForFrontEndProperties properties) {
    return new DefaultTokenExchangeService(webClient, sessionStorage, properties);
  }
}
