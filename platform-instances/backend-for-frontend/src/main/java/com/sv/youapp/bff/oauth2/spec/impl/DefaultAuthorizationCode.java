package com.sv.youapp.bff.oauth2.spec.impl;

import com.sv.youapp.bff.dto.AuthorizationResponse;
import com.sv.youapp.bff.oauth2.spec.AuthorizationCodeSpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Setter
@Accessors(fluent = true)
public class DefaultAuthorizationCode implements AuthorizationCodeSpec {
  @NonNull private String tokenEndpoint = "/oauth2/token";
  @Nullable private WebClient.RequestBodyUriSpec webClient;
  @NonNull private final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
  @Nullable private String clientId;
  @Nullable private String clientSecret;
  @NonNull private AuthenticationType authenticationType = AuthenticationType.NONE;

  private enum AuthenticationType {
    NONE,
    BASIC,
    POST
  }

  @Override
  public AuthorizationCodeSpec basic(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.authenticationType = AuthenticationType.BASIC;
    return this;
  }

  @Override
  public AuthorizationCodeSpec post(String clientId, String clientSecret) {
    Assert.hasText(clientId, "clientId cannot be empty");
    Assert.hasText(clientSecret, "clientSecret cannot be empty");
    body.add("client_id", clientId);
    body.add("client_secret", clientSecret);
    this.authenticationType = AuthenticationType.POST;
    return this;
  }

  @Override
  public AuthorizationCodeSpec webClient(WebClient webClient) {
    this.webClient = webClient.post();
    return this;
  }

  @Override
  public AuthorizationCodeSpec code(String code) {
    Assert.notNull(code, "code cannot be null");
    body.add("code", code);
    return this;
  }

  @Override
  public AuthorizationCodeSpec codeVerifier(String codeVerifier) {
    body.add("code_verifier", codeVerifier);
    return this;
  }

  @Override
  public AuthorizationCodeSpec redirectUri(String redirectUri) {
    body.add("redirect_uri", redirectUri);
    return this;
  }

  @Override
  public Mono<AuthorizationResponse> retrieve() {
    Assert.notNull(webClient, "webClient() is required and was not set");
    if (!body.containsKey("code")) {
      throw new IllegalStateException("code() is required and was not set");
    }
    if (!body.containsKey("redirect_uri")) {
      throw new IllegalStateException("redirectUri() is required and was not set");
    }
    body.add("grant_type", "authorization_code");
    WebClient.RequestBodySpec spec =
        webClient.uri(tokenEndpoint).contentType(MediaType.APPLICATION_FORM_URLENCODED);
    if (AuthenticationType.BASIC.equals(authenticationType)) {
      Assert.hasText(clientId, "clientId cannot be empty");
      Assert.hasText(clientSecret, "clientSecret cannot be empty");
      spec.headers(
          (HttpHeaders x) -> x.setBasicAuth(clientId, clientSecret, StandardCharsets.UTF_8));
    }
    return spec.body(BodyInserters.fromFormData(body)).retrieve().bodyToMono(AuthorizationResponse.class);
  }
}
