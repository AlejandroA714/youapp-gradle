package com.sv.youapp.bff.services.impl;

import com.sv.youapp.bff.services.TokenExchangeService;
import lombok.Builder;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Builder
public class DefaultTokenExchangeService implements TokenExchangeService {

	@Builder.Default
	private final String uri = "/oauth2/token";
	@Builder.Default
	private final String redirectUri = "/oauth2/callback";
	@Builder.Default
	private final WebClient client =  WebClient.create();

	@Override
	public Mono<Map> exchange(String code) {
		return client.post()
			.uri(uri)
			.headers(h -> h.setBasicAuth("oidc-client", "secret"))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(
				BodyInserters.fromFormData("grant_type", "authorization_code")
					.with("code", code)
					.with("redirect_uri", "http://192.168.1.24:8083" + redirectUri )
			)
			.retrieve()
			.bodyToMono(Map.class);
	}
}

