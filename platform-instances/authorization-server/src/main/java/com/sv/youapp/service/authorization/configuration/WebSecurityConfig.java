package com.sv.youapp.service.authorization.configuration;

import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.BodyFilters;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.core.QueryFilters;
import org.zalando.logbook.json.JsonBodyFilters;
import org.zalando.logbook.servlet.LogbookFilter;

@Configuration
public class WebSecurityConfig {
  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(
      HttpSecurity http, List<AbstractHttpConfigurer<?, HttpSecurity>> configurers, Logbook logbook)
      throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        OAuth2AuthorizationServerConfigurer.authorizationServer();

    http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        .with(authorizationServerConfigurer, config -> {})
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(Customizer.withDefaults());
    for (AbstractHttpConfigurer<?, HttpSecurity> c : configurers) {
      http.with(c, config -> {});
    }
    http.addFilterBefore(new LogbookFilter(logbook), SecurityContextHolderFilter.class);
    return http.build();
  }

  @Bean
  Logbook logbook() {
    return Logbook.builder()
        .headerFilter(HeaderFilters.authorization())
        .queryFilter(QueryFilters.replaceQuery("client_secret", "<redacted>"))
        .queryFilter(QueryFilters.replaceQuery("code", "<redacted>"))
        .bodyFilter(
            BodyFilter.merge(
                JsonBodyFilters.replaceJsonStringProperty(
                    Set.of(
                        "access_token",
                        "code",
                        "refresh_token",
                        "id_token",
                        "password",
                        "client_secret",
                        "code_verifier"),
                    "<redacted>"),
                BodyFilters.replaceFormUrlEncodedProperty(
                    Set.of(
                        "access_token",
                        "code",
                        "refresh_token",
                        "id_token",
                        "password",
                        "client_secret",
                        "code_verifier"),
                    "<redacted>")))
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(Customizer.withDefaults());
    return http.build();
  }
}
