package com.sv.youapp.bff.internal;

import com.sv.youapp.bff.services.*;
import com.sv.youapp.bff.services.impl.*;
import lombok.*;
import lombok.experimental.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.*;
import org.springframework.web.util.*;
import reactor.core.publisher.*;
import com.sv.youapp.bff.services.TokenExchangeService.AuthorizationCodeRequestSpec;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Consumer;


@Accessors(fluent = true)
public class DefaultAuthorizationCodeRequest implements AuthorizationCodeRequestSpec {
	@NonNull
	private final String host;
	@Getter
	private final DefaultRequestSpec request = new DefaultRequestSpec(this);
	private static final SecureRandom SR = new SecureRandom();

	public DefaultAuthorizationCodeRequest(@NonNull String host) {
		this.host = host;
	}

	@Override
	public TokenExchangeService.AuthorizationCodeRequestSpec request(Consumer<? super TokenExchangeService.RequestSpec<?>> dsl) {
		dsl.accept(request);
		return this;
	}

	@Override
	public Mono<Void> redirect(ServerHttpResponse res) {
		//TODO: REGISTER NONCE CODE_VERIFIER Y STATE for next step

		final String state = newState();

		final String scope = request.scopes();

		String nonce = null;

		if(scope != null && scope.contains("openid")) {
			nonce = newNonce();
		}

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(host)
			.path(request.authorizationUri())
			.queryParam("state", state)
			.queryParam("client_id","oidc-client")
			.queryParam("redirect_uri", request.redirectUri())
			.queryParam("response_type", request.responseType())
			.queryParam("response_mode", request.responseMode())
			.queryParamIfPresent("scope", Optional.ofNullable(scope))
			.queryParamIfPresent("nonce", Optional.ofNullable(nonce));

			//.queryParamIfPresent("code_challenge", Optional.ofNullable("asd123asd"))
			//.queryParamIfPresent("code_challenge_method", Optional.ofNullable("plain"))
			String url = uriBuilder
			.encode(StandardCharsets.UTF_8)
			.build().toUriString();
		return redirect(res, url);
	}

	public static String newState() {
		return newNonce();
	}

	public static String newNonce() {
		byte[] buf = new byte[32];
		SR.nextBytes(buf);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
	}

	private Mono<Void> redirect(ServerHttpResponse res, String url) {
		res.setStatusCode(HttpStatus.FOUND);
		res.getHeaders().setLocation(URI.create(url));
		res.getHeaders().add("Cache-Control", "no-store");
		return res.setComplete();
	}
}
