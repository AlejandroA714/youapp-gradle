package com.sv.youapp.common.authorization.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Set;

@Getter
@Setter
public class NativeAuthentication implements Authentication {
	public static final AuthorizationGrantType NATIVE_GRANT_TYPE = new AuthorizationGrantType("urn:ietf:params:oauth:grant-type:native");

	private String username;
	private final String password;
	private final Authentication clientPrincipal;
	private final String state;
	private final Set<GrantedAuthority> scopes;
	private Set<GrantedAuthority> granted;
	private boolean auth = false;

	public NativeAuthentication(String username, String password, Authentication clientPrincipal, String state, Set<GrantedAuthority> scopes, Set<GrantedAuthority> granted) {
		this.username = username;
		this.password = password;
		this.clientPrincipal = clientPrincipal;
		this.state = state;
		this.scopes = scopes;
		this.granted = granted;
	}

	@Override
	public Set<GrantedAuthority> getAuthorities() {
		return granted;
	}

	@Override
	public Object getCredentials() {
		return password;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return username;
	}

	@Override
	public boolean isAuthenticated() {
		return auth;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) {
		this.auth = isAuthenticated;
	}

	@Override
	public String getName() {
		return username;
	}
}
