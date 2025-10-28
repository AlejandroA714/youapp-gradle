package com.sv.youapp.bff.internal;

import com.sv.youapp.bff.enums.ResponseMode;
import com.sv.youapp.bff.enums.ResponseType;
import com.sv.youapp.bff.services.TokenExchangeService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;
import com.sv.youapp.bff.services.TokenExchangeService.AuthorizationCodeRequestSpec;
import com.sv.youapp.bff.services.TokenExchangeService.ReturnableRequestSpec;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter(AccessLevel.PACKAGE)
@Accessors(fluent = true)
public class DefaultRequestSpec implements ReturnableRequestSpec<AuthorizationCodeRequestSpec>{
	@NonNull
	private String authorizationUri = "/oauth2/authorize";
	@NonNull
	private String redirectUri = "/oauth2/callback";
	@NonNull
	private Set<String> scopes = new HashSet<>();
	@Nullable
	private String state;
	@Nullable
	private String nonce;
	@NonNull
	private ResponseType responseType = ResponseType.CODE;
	@NonNull
	private ResponseMode responseMode = ResponseMode.FORM_POST;
	@NonNull
	private final AuthorizationCodeRequestSpec parent;

	public DefaultRequestSpec(@NonNull AuthorizationCodeRequestSpec parent) {
		this.parent = parent;
	}

	@Override
	public AuthorizationCodeRequestSpec and() {
		return parent;
	}

	public String scopes(){
		if(this.scopes.isEmpty()) return null;
		return this.scopes.stream().filter(Objects::nonNull)
			.filter((String s) -> !s.isBlank()).collect(Collectors.joining(" "));
	}

	@Override
	public DefaultRequestSpec scopes(String... scope) {
		this.scopes.addAll(Arrays.stream(scope).toList());
		return this;
	}

	@Override
	public DefaultRequestSpec scopes(Set<String> scopes) {
		this.scopes.addAll(scopes);
		return this;
	}

	@Override
	public ReturnableRequestSpec<AuthorizationCodeRequestSpec> oidc() {
		this.scopes.add("openid");
		//TODO: GENERATE NONCE
		return this;
	}

	@Override
	public ReturnableRequestSpec<AuthorizationCodeRequestSpec> pkce() {
		//TODO: GENERATE CODE_VERIFIER AND CODE CHALLENGE
		return this;
	}
}
