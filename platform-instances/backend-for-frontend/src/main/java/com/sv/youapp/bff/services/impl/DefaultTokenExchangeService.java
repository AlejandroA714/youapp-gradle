package com.sv.youapp.bff.services.impl;

import static com.sv.youapp.bff.internal.RequestUtils.encode256UrlSafe;

import com.sv.youapp.bff.configuration.ServerPort;
import com.sv.youapp.bff.dto.AuthorizationResponse;
import com.sv.youapp.bff.dto.SessionRequest;
import com.sv.youapp.bff.internal.RequestUtils;
import com.sv.youapp.bff.oauth2.spec.RequestSpec;
import com.sv.youapp.bff.services.SessionStorage;
import com.sv.youapp.bff.services.TokenExchangeService;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Builder
public class DefaultTokenExchangeService implements TokenExchangeService {

  @Builder.Default private final String uri = "/oauth2/token";
  @Builder.Default private final String redirectUri = "/oauth2/callback";
  @Builder.Default private final WebClient client = WebClient.create("http://localhost:8082");
  @NonNull private final SessionStorage sessionStorage = new InMemorySessionStorage();

  @Override
  public Mono<AuthorizationResponse> exchange(String code) {
    // TODO: VALIDATE STATE MATCHES
    var aa = sessionStorage.get("90901");

    return TokenExchangeService.authorizationCode()
			.webClient(client)
			.code(code)
			.codeVerifier(aa.codeVerifier())
        .basic("oidc-client", "pass")
        .redirectUri(aa.redirectUri())
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
        TokenExchangeService.authorizationCodeRequest("http://localhost:8082")
            .clientId("oidc-client")
					.request((RequestSpec<?> x) -> x.scopes(scopes)
						.state(state)
						.redirectUri(redirectUri))
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
