package com.sv.youapp.bff.services;

import com.sv.youapp.bff.enums.ResponseMode;
import com.sv.youapp.bff.enums.ResponseType;
import com.sv.youapp.bff.internal.DefaultAuthorizationCodeRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface TokenExchangeService {

    Mono<Map> exchange(String code);

	static AuthorizationCodeRequestSpec authorizationCodeRequest(String host) {
		return new DefaultAuthorizationCodeRequest(host);
	}

	interface GrantCredentialsSpec<T> {
		T username(String username);
	}

	 interface AuthorizationCodeSpec {
		 AuthorizationCodeSpec code(String code);

		 Mono<Void> redirect();

		 Mono<String> retrieve();
	}

	/**
	 * An interface representing the specification for an authorization code request.
	 * This interface provides methods for configuring and managing the authorization code
	 * request, including setting the request parameters and handling redirections.
	 */
	interface AuthorizationCodeRequestSpec {

		ReturnableRequestSpec<AuthorizationCodeRequestSpec> request();

		AuthorizationCodeRequestSpec request(Consumer<? super RequestSpec<?>> dsl);

		Mono<Void> redirect(ServerHttpResponse res);
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
		 * Sets the scopes for the authorization request. Scopes represent the specific
		 * permissions or access levels that the client application is requesting from the
		 * resource owner. A scope is typically associated with a certain set of operations
		 * or a subset of information the client is allowed to access.
		 *
		 * @param scope an array of strings, each representing an individual scope to include
		 *              in the authorization request
		 * @return the current {@code RedirectSpec} instance
		 */
		T scopes(String... scope);

		/**
		 * Sets the scopes for the authorization request. Scopes represent the specific
		 * resources and permissions that the client application is requesting access to.
		 *
		 * @param scopes a collection of strings, each representing an individual scope to include
		 *               in the authorization request
		 * @return the current {@code RedirectSpec} instance
		 */
		T scopes(Set<String> scopes);

		//TODO: EVALUATE
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
		 * Configures the authorization request to use OpenID Connect (OIDC).
		 * This method indicates that the client application is performing an
		 * OpenID Connect-based authorization flow, typically involving the
		 * retrieval of ID tokens and/or user info from the authorization server.
		 *
		 * @return the updated {@code RequestSpec} instance with OIDC configuration applied
		 */
		T oidc();

		/**
		 * Configures the authorization request to utilize the Proof Key for Code Exchange (PKCE) mechanism.
		 * PKCE enhances the security of authorization code flows by requiring a dynamically generated
		 * verifier and challenge pair, which is validated during the token exchange process.
		 *
		 * @return the updated {@code RequestSpec} instance with PKCE configuration applied
		 */
		T pkce();
	}

}
