package com.sv.youapp.common.authorization.dto;

import java.util.Set;
import lombok.NonNull;
import org.springframework.lang.Nullable;

public record SessionRequest(
    @Nullable String state,
    @NonNull Set<String> scope,
    @NonNull String redirectUri,
    @Nullable String codeVerifier,
    @Nullable String nonce) {
  public SessionRequest {
    scope = Set.copyOf(scope);
  }
}
