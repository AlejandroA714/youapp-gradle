package com.sv.youapp.bff.oauth2.spec.impl;

import com.sv.youapp.bff.enums.ResponseMode;
import com.sv.youapp.bff.enums.ResponseType;
import com.sv.youapp.bff.oauth2.spec.AuthorizationCodeRequestSpec;
import com.sv.youapp.bff.oauth2.spec.RequestSpec.ReturnableRequestSpec;
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
  @Nullable Set<String> scopes = null;
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

  @NonNull
  public Set<String> scopes() {
    if (this.scopes == null) {
      this.scopes = new HashSet<>();
    }
    return this.scopes;
  }

  public String joinedScopes() {
    if (this.scopes != null) {
      return this.scopes.stream()
          .filter(Objects::nonNull)
          .filter((String s) -> !s.isBlank())
          .collect(Collectors.joining(" "));
    }
    return null;
  }

  @Override
  public ReturnableRequestSpec<AuthorizationCodeRequestSpec> scope(String... scope) {
    if (this.scopes == null) {
      this.scopes = new HashSet<>();
    }
    try {
      this.scopes.addAll(Set.of(scope));
    } catch (UnsupportedOperationException e) {
      this.scopes = new HashSet<>(this.scopes);
      this.scopes.addAll(Set.of(scope));
    }
    this.scopes.addAll(Set.of(scope));
    return this;
  }
}
