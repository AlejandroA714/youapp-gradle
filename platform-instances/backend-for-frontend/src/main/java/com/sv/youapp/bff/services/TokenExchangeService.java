package com.sv.youapp.bff.services;

import com.sv.youapp.bff.dto.AuthorizationResponse;
import com.sv.youapp.bff.oauth2.spec.AuthorizationCodeRequestSpec;
import com.sv.youapp.bff.oauth2.spec.AuthorizationCodeSpec;
import com.sv.youapp.bff.oauth2.spec.impl.DefaultAuthorizationCode;
import com.sv.youapp.bff.oauth2.spec.impl.DefaultAuthorizationCodeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

public interface TokenExchangeService {

  Mono<AuthorizationResponse> exchange(String code);

  Mono<Void> start(ServerHttpResponse res);

  static AuthorizationCodeRequestSpec authorizationCodeRequest(String host) {
    return new DefaultAuthorizationCodeRequest(host);
  }

  static AuthorizationCodeSpec authorizationCode() {
    return new DefaultAuthorizationCode();
  }

	static Mono<Void> redirect(ServerHttpResponse res, String url){
		res.setStatusCode(HttpStatus.FOUND);
		res.getHeaders().setLocation(URI.create(url));
		res.getHeaders().add("Cache-Control", "no-store");
		return res.setComplete();
	}
}
