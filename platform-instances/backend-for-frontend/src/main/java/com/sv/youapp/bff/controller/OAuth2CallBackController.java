package com.sv.youapp.bff.controller;

import com.sv.youapp.bff.services.TokenExchangeService;
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

  @NonNull private final TokenExchangeService tokenExchangeService;

  @GetMapping("/callback")
  @ResponseStatus(HttpStatus.FOUND)
  public Mono<Void> callback(
      @RequestParam("code") String code,
      @RequestParam("state") String state,
      ServerHttpResponse res) {
    // TODO: SOLVE WITH /.well-know/assetslink.json
    return tokenExchangeService
        .exchange(code, state)
        .flatMap(x -> TokenExchangeService.redirect(res, "youapp://oauth2?sid=" + x.accessToken()));
  }

  @GetMapping("/login")
  @ResponseStatus(HttpStatus.FOUND)
  public Mono<Void> login(ServerHttpResponse res) {
    return tokenExchangeService.start(res);
  }
}
