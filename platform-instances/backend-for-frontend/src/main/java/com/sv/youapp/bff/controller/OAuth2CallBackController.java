package com.sv.youapp.bff.controller;

import com.sv.youapp.bff.dto.AuthorizationResponse;
import com.sv.youapp.bff.services.TokenExchangeService;
import java.net.URI;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuth2CallBackController {

	@NonNull
  private final TokenExchangeService tokenExchangeService;

  @GetMapping("/callback")
  @ResponseStatus(HttpStatus.FOUND)
  public Mono<AuthorizationResponse> callback(
      @RequestParam("code") String code,
      @RequestParam("state") String state,
      ServerHttpResponse res) {
    return tokenExchangeService
        .exchange(code);
  }

  @GetMapping("/login")
  @ResponseStatus(HttpStatus.FOUND)
  public Mono<Void> login(ServerHttpResponse res) {
    return tokenExchangeService.start(res);
  }

  private Mono<Void> redirect(ServerHttpResponse res, String url) {
    res.setStatusCode(HttpStatus.FOUND);
    res.getHeaders().setLocation(URI.create(url));
    res.getHeaders().add("Cache-Control", "no-store");
    return res.setComplete();
  }
}
