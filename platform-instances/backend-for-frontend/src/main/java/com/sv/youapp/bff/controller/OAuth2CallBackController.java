package com.sv.youapp.bff.controller;

import com.sv.youapp.bff.enums.ResponseMode;
import com.sv.youapp.bff.enums.ResponseType;
import com.sv.youapp.bff.services.TokenExchangeService;
import org.apache.el.parser.Token;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/oauth2")
public class OAuth2CallBackController {

	private final TokenExchangeService tokenExchangeService;

	public OAuth2CallBackController(TokenExchangeService tokenExchangeService) {
		this.tokenExchangeService = tokenExchangeService;
	}

	@GetMapping("/callback")
	@ResponseStatus(HttpStatus.FOUND)
	public Mono<Void> callback(
		@RequestParam("code") String code,
		ServerHttpResponse res
	) {
		// Intercambia el code (opcional) y redirige al esquema personalizado
		return tokenExchangeService.exchange(code)
			.flatMap(x -> redirect(res, "youapp://oauth2?sid=" + code));
	}

	@GetMapping("/login")
	@ResponseStatus(HttpStatus.FOUND)
	public Mono<Void> login(ServerHttpResponse res) {
		return TokenExchangeService.authorizationCodeRequest("http://localhost:8082")
			//.request().authorizationUri("/oauth2/pruebas")
			//.responseMode(ResponseMode.FORM_POST)
			//.responseType(ResponseType.CODE)
			//.scopes("profile","email")
			.request()
			//.oidc()
			.redirectUri("https://oidcdebugger.com/debug")
			.and().request()
			.and()
			.redirect(res);
	}

	private Mono<Void> redirect(ServerHttpResponse res, String url) {
		res.setStatusCode(HttpStatus.FOUND);
		res.getHeaders().setLocation(URI.create(url));
		res.getHeaders().add("Cache-Control", "no-store");
		return res.setComplete();
	}
}
