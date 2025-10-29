package com.sv.youapp.bff.services;

import com.sv.youapp.bff.enums.ResponseMode;
import com.sv.youapp.bff.enums.ResponseType;
import com.sv.youapp.bff.internal.DefaultAuthorizationCodeRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.util.UriComponents;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface TokenExchangeService {

    Mono<Map> exchange(String code);

	Mono<Void> init(ServerHttpResponse res);

	static AuthorizationCodeRequestSpec authorizationCodeRequest(String host) {
		return new DefaultAuthorizationCodeRequest(host);
	}

	/**
	 * An interface representing the specification for an authorization code request.
	 * This interface provides methods for configuring and managing the authorization code
	 * request, including setting the request parameters and handling redirections.
	 */
	interface AuthorizationCodeRequestSpec {

		ReturnableRequestSpec<AuthorizationCodeRequestSpec> request();

		AuthorizationCodeRequestSpec request(Consumer<? super RequestSpec<?>> dsl);

		AuthorizationCodeRequestSpec oidc();

		AuthorizationCodeRequestSpec oidc(Consumer<OidcRequestSpec> dsl);

		AuthorizationCodeRequestSpec pkce();

		AuthorizationCodeRequestSpec pkce(Consumer<ProofKeyForCodeExchangeRequestSpec> dsl);

		AuthorizationCodeRequestSpec clientId(String state);

		AuthorizationCodeRequestSpec scope(String... scope);

		AuthorizationCodeRequestSpec scopes(Set<String> scope);

		UriComponents build();
	}

	interface ProofKeyForCodeExchangeRequestSpec {
		ProofKeyForCodeExchangeRequestSpec codeVerifier(String codeVerifier);
	}

	interface OidcRequestSpec {
		OidcRequestSpec nonce(String nonce);
	}

	interface RequestSpec<T extends RequestSpec<T>>{
		/**
		 * Sets the authorization URI to be used for the authorization request. The authorization URI
		 * specifies the endpoint of the authorization server where the client application directs the user
		 * to initiate the authorization process.
		 *
		 * @param authorizationUri the URI of the authorization server to use for the authorization request
		 * @return the current {@code RedirectSpec} instance
		 */
		T authorizationUri(String authorizationUri);

		/**
		 * Sets the redirect URI for the authorization request. The redirect URI is the endpoint
		 * to which the authorization server will redirect the user agent after the authorization
		 * code, token, or ID token has been obtained.
		 *
		 * @param redirectUri the URI to be used for the redirection endpoint
		 * @return the current {@code RedirectSpec} instance
		 */
		T redirectUri(String redirectUri);

		/**
		 * Sets the scopes to be included in the authorization request. Scopes define the level
		 * of access that the client application is requesting from the resource owner.
		 *
		 * @param scope one or more scopes to be requested during the authorization process
		 * @return the current instance of the request specification
		 */
		T scope(String... scope);

		/**
		 * Sets the scopes for the authorization request. Scopes define the permissions and level of access
		 * that the client application is requesting from the resource owner. This method allows setting multiple scopes
		 * in the form of a {@code Set}.
		 *
		 * @param scope a {@code Set} of scope strings to be included in the authorization request
		 * @return the instance of the current object
		 */
		T scopes(Set<String> scope);

		/**
		 * Sets the response type for the redirect request. The response type determines the authorization response
		 * flow to be used, such as returning an authorization code, token, or ID token.
		 *
		 * @param type the response type to be used for the authorization request, must be one of {@link ResponseType}
		 * @return the current {@code RedirectSpec} instance
		 */
		T responseType(ResponseType type);

		/**
		 * Sets the response mode for the redirect request. The response mode determines how the authorization response
		 * is returned to the client, such as through query parameters, form posts, or fragments.
		 *
		 * @param mode the response mode to be used for the authorization request, must be one of {@link ResponseMode}
		 * @return the current {@code RedirectSpec} instance
		 */
		T responseMode(ResponseMode mode);

		/**
		 * Configures the code challenge mechanism for a request. The code challenge is
		 * part of the PKCE (Proof Key for Code Exchange) protocol, which enhances the
		 * security of OAuth 2.0 authorization code flows by adding an additional
		 * verification layer during token exchange. This method typically sets or
		 * enables the code challenge value, which is derived from the code verifier.
		 *
		 * @return the updated instance of the corresponding request specification or chain.
		 */
		T codeChallengeMethod(String method);

		/**
		 * Computes or retrieves the code challenge for the request.
		 * The code challenge is a critical component in the PKCE (Proof Key for Code Exchange) protocol,
		 * which enhances security in OAuth 2.0 authorization flows by preventing code interception attacks.
		 * It is typically a hashed and encoded value derived from the code verifier.
		 *
		 * @return the code challenge value to be used in the authorization request
		 */
		T codeChallenge(String codeChallenge);

		T state(String state);

		T nonce(String nonce);
	}

	/**
	 * Represents a specification for a request in the form of a fluently configurable interface,
	 * making it usable for constructing and customizing request configurations in a chained manner.
	 *
	 * @param <P> the type of the parent specification to which this request specification belongs.
	 */
	interface ReturnableRequestSpec<P> extends RequestSpec<ReturnableRequestSpec<P>> {
		P and();
	}

}
