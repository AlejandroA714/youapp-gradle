package com.sv.youapp.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthorizationResponse(
	@JsonProperty(value = "access_token", required = true) String accessToken,
	@JsonProperty(value = "expires_in", required = true) Integer expiresIn,
	@JsonProperty(value = "token_type", required = true) String tokenType,
	@JsonProperty("refresh_token") String refreshToken,
	@JsonProperty(value = "id_token") String idToken,
	@JsonProperty("scope") String scope
) { }
