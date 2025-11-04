package com.sv.youapp.bff.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

public record SessionRequest(
   @Nullable String state,
   @NonNull Set<String> scope,
   @NonNull String redirectUri,
   @Nullable String codeVerifier,
   @Nullable String nonce)
{
	public SessionRequest {
		scope = Set.copyOf(scope);
	}
}
