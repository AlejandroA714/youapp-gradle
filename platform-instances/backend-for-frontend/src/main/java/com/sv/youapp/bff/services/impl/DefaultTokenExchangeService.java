package com.sv.youapp.bff.services.impl;

import com.sv.youapp.bff.services.TokenExchangeService;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

public record DefaultTokenExchangeService(WebClient webClient) implements TokenExchangeService {

	@Override
	public Mono<Map> exchange(String code) {
		return webClient.post()
			.uri("/oauth2/token")
			.headers(h -> h.setBasicAuth("oidc-client", "secret"))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(
				BodyInserters.fromFormData("grant_type", "authorization_code")
					.with("code", code)
					.with("redirect_uri", "http://192.168.1.24:8083/oauth2/callback")
			)
			.retrieve()
			.bodyToMono(Map.class);
	}
}

