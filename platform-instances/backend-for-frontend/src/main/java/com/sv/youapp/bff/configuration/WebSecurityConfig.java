package com.sv.youapp.bff.configuration;

import com.sv.youapp.bff.services.TokenExchangeService;
import com.sv.youapp.bff.services.impl.DefaultTokenExchangeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig {
  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(ServerHttpSecurity.CorsSpec::disable)
        .authorizeExchange(ex -> ex.anyExchange().permitAll())
        .build();
  }

  @Bean
  public TokenExchangeService tokenExchangeService() {
    //        var dd =  DefaultTokenExchangeService.builder()
    //			.web(x -> x.host("http://localhost:8084"))
    //			.build();
    // dd.exchange("ADASDA").block();
    return DefaultTokenExchangeService.builder().build();
  }

  //	@Bean
  //	public Cache<String, Object> cache() {
  //	}
}
