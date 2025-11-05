package com.sv.youapp.bff.oauth2.spec;

import com.sv.youapp.bff.dto.AuthorizationResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public interface AuthorizationCodeSpec {

  /**
   * Sets the authorization code to be used in the OAuth 2.0 Authorization Code flow. The
   * authorization code is typically obtained from the authorization server after the user
   * authorizes the client.
   *
   * @param code the authorization code provided by the authorization server
   * @return the current {@code AuthorizationCodeSpec} instance for method chaining
   */
  AuthorizationCodeSpec code(String code);

  /** Sets the code verifier to be used in the OAuth */
  AuthorizationCodeSpec codeVerifier(String state);

  /**
   * Sets the redirect URI to be used for the OAuth 2.0 Authorization Code flow. This URI is used by
   * the authorization server to redirect the user-agent after granting or denying access. It must
   * match one of the registered redirect URIs for the client.
   *
   * @param redirectUri the redirect URI to be used
   * @return the current {@code AuthorizationCodeSpec} instance for method chaining
   */
  AuthorizationCodeSpec redirectUri(String redirectUri);

  /**
   * Configures basic authentication for the OAuth 2.0 Authorization Code flow using the provided
   * client ID and client secret. This method sets the client credentials required to authenticate
   * with the authorization server.
   *
   * @param clientId the client identifier registered with the authorization server
   * @param clientSecret the client secret associated with the given client ID
   * @return the current {@code AuthorizationCodeSpec} instance for method chaining
   */
  AuthorizationCodeSpec basic(String clientId, String clientSecret);

  /**
   * Sends a POST request as part of the OAuth 2.0 Authorization Code flow using the provided client
   * credentials. This method facilitates the execution of the token exchange process, utilizing the
   * given client ID and client secret to authenticate with the authorization server.
   *
   * @param clientId the client identifier registered with the authorization server
   * @param clientSecret the client secret associated with the given client ID
   * @return the current {@code AuthorizationCodeSpec} instance for method chaining
   */
  AuthorizationCodeSpec post(String clientId, String clientSecret);

  /**
   * Sets the token endpoint URI to be used for exchanging the authorization code for an access
   * token and other authentication details in the OAuth 2.0 Authorization Code flow.
   *
   * @param tokenEndpoint the URI of the token endpoint, which is the server's endpoint for handling
   *     token exchange operations
   * @return the current {@code AuthorizationCodeSpec} instance for method chaining
   */
  AuthorizationCodeSpec tokenEndpoint(String tokenEndpoint);

  /**
   * Configures the {@link WebClient} to be used for making requests in the context of the
   * Authorization Code flow. This allows the customization of the underlying HTTP client used for
   * token exchange operations, enabling fine-grained control over request execution (e.g., setting
   * timeouts, adding custom headers, etc.).
   *
   * @param webClient the {@link WebClient} to be used for executing HTTP requests
   * @return the current {@code AuthorizationCodeSpec} instance for method chaining
   */
  AuthorizationCodeSpec webClient(WebClient webClient);

  /**
   * Executes the token exchange process for the OAuth 2.0 Authorization Code flow and retrieves the
   * authorization response. The response typically contains the access token, refresh token, token
   * type, and other relevant authentication details.
   *
   * @return a {@link Mono} that emits the {@link AuthorizationResponse} upon successful retrieval
   *     or an error if the exchange fails.
   */
  Mono<AuthorizationResponse> retrieve();
}
