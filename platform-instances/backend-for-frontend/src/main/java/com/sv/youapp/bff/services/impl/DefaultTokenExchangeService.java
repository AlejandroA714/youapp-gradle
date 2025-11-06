package com.sv.youapp.bff.services.impl;

import static com.sv.youapp.bff.utils.RequestUtils.encode256UrlSafe;

import com.sv.youapp.bff.configuration.ServerPort;
import com.sv.youapp.bff.dto.AuthorizationResponse;
import com.sv.youapp.bff.oauth2.spec.AuthorizationCodeRequestSpec.ProofKeyForCodeExchangeRequestSpec;
import com.sv.youapp.bff.oauth2.spec.RequestSpec;
import com.sv.youapp.bff.properties.BackEndForFrontEndProperties;
import com.sv.youapp.bff.services.TokenExchangeService;
import com.sv.youapp.bff.utils.RequestUtils;
import com.sv.youapp.common.authorization.dto.SessionRequest;
import com.sv.youapp.common.authorization.services.SessionStorage;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

public record DefaultTokenExchangeService(
    @NonNull WebClient webClient,
    @NonNull SessionStorage sessionStorage,
    @NonNull BackEndForFrontEndProperties properties)
    implements TokenExchangeService {

  @Override
  public Mono<AuthorizationResponse> exchange(String code, String state) {
    SessionRequest request = sessionStorage.get(state);
    // IF DOESNT EXITS, CANT CONTINUE
    if (request == null) {
      throw new IllegalStateException("CRSF DETECTED, SESSION EXPIRED OR INVALID");
    }
    return TokenExchangeService.authorizationCode()
        .webClient(webClient)
        .code(code)
        .codeVerifier(request.codeVerifier())
        .basic(properties.clientId(), properties.clientSecret())
        .redirectUri(request.redirectUri())
        .retrieve();
  }

  @Override
  public Mono<Void> start(ServerHttpResponse res) {
    final String state = encode256UrlSafe();
    final String nonce = encode256UrlSafe();
    final String codeVerifier = encode256UrlSafe();
    final String redirectUri = resolveSelf();
    final Set<String> scopes = new HashSet<>();
    UriComponents uri =
        TokenExchangeService.authorizationCodeRequest(properties.url().toString())
            // TODO: RECEIVE FROM APP?
            .clientId(properties().clientId())
            .request((RequestSpec<?> x) -> x.scopes(scopes).state(state).redirectUri(redirectUri))
            .pkce((ProofKeyForCodeExchangeRequestSpec pkce) -> pkce.codeVerifier(codeVerifier))
            .build();
    sessionStorage.save(state, new SessionRequest(state, scopes, redirectUri, codeVerifier, nonce));
    return TokenExchangeService.redirect(res, uri.toUriString());
  }

  private String resolveSelf() {
    String ip = RequestUtils.resolveNonLoopbackAddress();
    UriComponents uri =
        UriComponentsBuilder.fromUriString(String.format("http://%s:%s", ip, ServerPort.getPort()))
            .path("/oauth2/callback")
            .encode(StandardCharsets.UTF_8)
            .build();
    return uri.toString();
  }
}
