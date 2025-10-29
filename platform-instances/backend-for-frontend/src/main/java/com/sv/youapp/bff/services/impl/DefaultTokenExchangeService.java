package com.sv.youapp.bff.services.impl;

import com.sv.youapp.bff.services.SessionStorage;
import com.sv.youapp.bff.services.TokenExchangeService;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.sv.youapp.bff.internal.RequestUtils.encode256UrlSafe;

@Builder
public class DefaultTokenExchangeService implements TokenExchangeService {

	@Builder.Default
	private final String uri = "/oauth2/token";
	@Builder.Default
	private final String redirectUri = "/oauth2/callback";
	@Builder.Default
	private final WebClient client =  WebClient.create();
	@NonNull
	private final SessionStorage sessionStorage = new InMemorySessionStorage();

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

	@Override
	public Mono<Void> init(ServerHttpResponse res) {

		final String state = encode256UrlSafe();
		final String nonce = encode256UrlSafe();
		final String codeVerifier = encode256UrlSafe();
		UriComponents uri = TokenExchangeService
			.authorizationCodeRequest("http://localhost:8082")
			.clientId("oidc-client")
			.request()
			.scope("profile","email")
			.state(state)
			.redirectUri("https://oidcdebugger.com/debug")
			.and()
			//.pkce(pkce -> pkce.codeVerifier(codeVerifier))
			//.oidc(oidc -> oidc.nonce(nonce))
			.build();

		return redirect(res, uri.toUriString());
	}

	private Mono<Void> redirect(ServerHttpResponse res, String url) {
		res.setStatusCode(HttpStatus.FOUND);
		res.getHeaders().setLocation(URI.create(url));
		res.getHeaders().add("Cache-Control", "no-store");
		return res.setComplete();
	}
}

