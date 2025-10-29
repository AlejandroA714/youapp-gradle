package com.sv.youapp.bff.internal;

import static com.sv.youapp.bff.internal.RequestUtils.codeChallengeS256;
import static com.sv.youapp.bff.internal.RequestUtils.encode256UrlSafe;
import static com.sv.youapp.bff.internal.RequestUtils.generateCodeVerifier;

import com.sv.youapp.bff.services.TokenExchangeService;
import com.sv.youapp.bff.services.TokenExchangeService.AuthorizationCodeRequestSpec;
import com.sv.youapp.bff.services.TokenExchangeService.ProofKeyForCodeExchangeRequestSpec;
import com.sv.youapp.bff.services.TokenExchangeService.RequestSpec;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Accessors(fluent = true)
public class DefaultAuthorizationCodeRequest implements AuthorizationCodeRequestSpec {
  @NonNull private final String host;
  @Getter private final DefaultRequestSpec request = new DefaultRequestSpec(this);
  private DefaultProofKeyForCodeRequest proofKeyForCodeRequest = null;
  private DefaultOidcRequest oidcRequest = null;
  @Setter private String clientId;

  public DefaultAuthorizationCodeRequest(@NonNull String host) {
    this.host = host;
  }

  @Override
  public AuthorizationCodeRequestSpec request(Consumer<? super RequestSpec<?>> dsl) {
    dsl.accept(request);
    return this;
  }

  @Override
  public AuthorizationCodeRequestSpec oidc() {
    this.request.scope("openid");
    this.request.nonce(encode256UrlSafe());
    return this;
  }

  @Override
  public AuthorizationCodeRequestSpec pkce() {
    final String codeVerifier = generateCodeVerifier();
    request.codeChallenge(codeChallengeS256(codeVerifier)).codeChallengeMethod("S256");
    return this;
  }

  @Override
  public AuthorizationCodeRequestSpec pkce(Consumer<ProofKeyForCodeExchangeRequestSpec> dsl) {
    if (this.proofKeyForCodeRequest == null)
      this.proofKeyForCodeRequest = new DefaultProofKeyForCodeRequest();
    dsl.accept(this.proofKeyForCodeRequest);
    if (this.proofKeyForCodeRequest.codeVerifier != null) {
      request.codeChallenge(codeChallengeS256(this.proofKeyForCodeRequest.codeVerifier));
      request.codeChallengeMethod("S256");
    }
    return this;
  }

  @Override
  public AuthorizationCodeRequestSpec oidc(Consumer<TokenExchangeService.OidcRequestSpec> dsl) {
    if (this.oidcRequest == null) this.oidcRequest = new DefaultOidcRequest();
    dsl.accept(this.oidcRequest);
    if (this.oidcRequest.nonce != null) {
      request.nonce(this.oidcRequest.nonce);
    }
    return this;
  }

  @Override
  public AuthorizationCodeRequestSpec scope(String... scope) {
    this.request.scopes(Set.of(scope));
    return this;
  }

  @Override
  public AuthorizationCodeRequestSpec scopes(Set<String> scope) {
    this.request.scopes(scope);
    return this;
  }

  @Override
  public UriComponents build() {
    return UriComponentsBuilder.fromUriString(host)
        .path(request.authorizationUri())
        .queryParam("state", request.state())
        .queryParam("client_id", clientId)
        .queryParam("redirect_uri", request.redirectUri())
        .queryParam("response_type", request.responseType())
        .queryParam("response_mode", request.responseMode())
        .queryParamIfPresent("scope", Optional.ofNullable(request.scopes()))
        .queryParamIfPresent("nonce", Optional.ofNullable(request.nonce()))
        .queryParamIfPresent("code_challenge", Optional.ofNullable(request.codeChallenge()))
        .queryParamIfPresent(
            "code_challenge_method", Optional.ofNullable(request.codeChallengeMethod()))
        .encode(StandardCharsets.UTF_8)
        .build();
  }

  @Setter
  @Accessors(fluent = true)
  public static class DefaultProofKeyForCodeRequest implements ProofKeyForCodeExchangeRequestSpec {
    @Nullable private String codeVerifier;
  }

  @Setter
  @Accessors(fluent = true)
  public static class DefaultOidcRequest implements TokenExchangeService.OidcRequestSpec {
    @Nullable private String nonce;
  }
}
