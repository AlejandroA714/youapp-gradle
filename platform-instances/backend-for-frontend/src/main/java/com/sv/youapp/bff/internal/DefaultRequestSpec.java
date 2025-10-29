package com.sv.youapp.bff.internal;

import com.sv.youapp.bff.enums.ResponseMode;
import com.sv.youapp.bff.enums.ResponseType;
import com.sv.youapp.bff.services.TokenExchangeService.AuthorizationCodeRequestSpec;
import com.sv.youapp.bff.services.TokenExchangeService.ReturnableRequestSpec;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

@Setter
@Getter(AccessLevel.PACKAGE)
@Accessors(fluent = true)
public class DefaultRequestSpec implements ReturnableRequestSpec<AuthorizationCodeRequestSpec> {
  @Nullable private String state;
  @Nullable private String nonce;
  @Nullable private String codeChallenge;
  @Nullable private String codeChallengeMethod;
  @NonNull private Set<String> scopes = new HashSet<>();
  @NonNull private String redirectUri = "/oauth2/callback";
  @NonNull private final AuthorizationCodeRequestSpec parent;
  @NonNull private String authorizationUri = "/oauth2/authorize";
  @NonNull private ResponseType responseType = ResponseType.CODE;
  @NonNull private ResponseMode responseMode = ResponseMode.FORM_POST;

  public DefaultRequestSpec(@NonNull AuthorizationCodeRequestSpec parent) {
    this.parent = parent;
  }

  @Override
  public AuthorizationCodeRequestSpec and() {
    return parent;
  }

  @Override
  public ReturnableRequestSpec<AuthorizationCodeRequestSpec> scope(String... scope) {
    this.scopes.addAll(Set.of(scope));
    return this;
  }

  @Override
  public ReturnableRequestSpec<AuthorizationCodeRequestSpec> scopes(Set<String> scope) {
    this.scopes.addAll(scope);
    return this;
  }

  public String scopes() {
    if (this.scopes.isEmpty()) return null;
    return this.scopes.stream()
        .filter(Objects::nonNull)
        .filter((String s) -> !s.isBlank())
        .collect(Collectors.joining(" "));
  }
}
