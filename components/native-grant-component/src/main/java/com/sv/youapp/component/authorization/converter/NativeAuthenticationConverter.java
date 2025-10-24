package com.sv.youapp.component.authorization.converter;

import com.sv.youapp.common.authorization.authentication.NativeAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.sv.youapp.common.authorization.authentication.NativeAuthentication.NATIVE_GRANT_TYPE;

public class NativeAuthenticationConverter implements AuthenticationConverter {

	@Override
	public Authentication convert(HttpServletRequest request) {
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		if (!NATIVE_GRANT_TYPE.getValue().equals(grantType)) {
			return null;
		}

		Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
		MultiValueMap<String, String> parameters = getParameters(request);

		// username: (REQUIRED)
		String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
		if (username == null) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
		}

		// password: (REQUIRED)
		String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
		if (password == null) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
		}

		// state: (OPTIONAL)
		String state = parameters.getFirst(OAuth2ParameterNames.STATE);
		if (StringUtils.hasText(state) && parameters.get(OAuth2ParameterNames.STATE).size() != 1) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
		}

		// scope (OPTIONAL)
		Set<String> scopes = null;
		String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
		if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
			String error = "OAuth 2.0 Parameter: " + OAuth2ParameterNames.SCOPE;
			throwError(error);
		}
		if (StringUtils.hasText(scope)) {
			if (scope.contains(",")) {
				String error = "OAuth 2.0 Parameter: " + OAuth2ParameterNames.SCOPE + " must be space separated";
				throwError(error);
			}
			String[] parts = StringUtils.delimitedListToStringArray(scope, " ");
			scopes = new HashSet<>(Arrays.asList(parts));
		}

		return new NativeAuthentication(
			username,
			password,
			clientPrincipal,
			state,
			(scopes != null ? toAuthorities(scopes) : new HashSet<>()),
			null
		);
	}

	private static Set<GrantedAuthority> toAuthorities(Set<String> scopes) {
		Set<GrantedAuthority> out = new HashSet<>();
		for (String s : scopes) {
			out.add(new SimpleGrantedAuthority(s));
		}
		return out;
	}

	private static void throwError(String message) {
		throw new OAuth2AuthenticationException(
			new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, message, null)
		);
	}

	private static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		String queryString = StringUtils.hasText(request.getQueryString()) ? request.getQueryString() : "";
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String key = entry.getKey();
			String[] values = entry.getValue();
			if (!queryString.contains(key) && values != null) {
				for (String v : values) {
					parameters.add(key, v);
				}
			}
		}
		return parameters;
	}
}
