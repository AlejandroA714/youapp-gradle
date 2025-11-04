package com.sv.youapp.bff.oauth2.spec;

import com.sv.youapp.bff.oauth2.spec.RequestSpec.ReturnableRequestSpec;
import java.util.function.Consumer;
import org.springframework.web.util.UriComponents;

/**
 * An interface representing the specification for an authorization code request. This interface
 * provides methods for configuring and managing the authorization code request, including setting
 * the request parameters and handling redirections.
 */
public interface AuthorizationCodeRequestSpec {

	/**
	 * Provides a specification for defining and configuring the details of an authorization
	 * code request. This method allows fluent configuration of request parameters and returns
	 * a specification object for further customization.
	 *
	 * @return a {@code ReturnableRequestSpec} instance that allows further customization of
	 *         the authorization code request and supports returning to the parent
	 *         specification {@code AuthorizationCodeRequestSpec}.
	 */
  ReturnableRequestSpec<AuthorizationCodeRequestSpec> request();

	/**
	 * Configures the request using the provided {@code Consumer}.
	 *
	 * This method applies a customization function to the {@link RequestSpec} of the authorization
	 * code request. The customization allows setting parameters, headers, or any other adjustments
	 * necessary to define the request details.
	 *
	 * @param dsl a {@code Consumer} that performs custom configurations on the {@link RequestSpec}
	 * @return the current {@code AuthorizationCodeRequestSpec} instance for further configuration
	 */
  AuthorizationCodeRequestSpec request(Consumer<? super RequestSpec<?>> dsl);

	/**
	 * Configures the current authorization code request for OpenID Connect (OIDC) compliance.
	 *
	 * This method enables the setup of relevant parameters and behaviors required for OIDC-specific
	 * authentication and session management, including support for ID tokens and related OIDC
	 * functionality.
	 *
	 * @return the current {@code AuthorizationCodeRequestSpec} instance for further configuration.
	 */
  AuthorizationCodeRequestSpec oidc();

	/**
	 * Configures the current authorization code request for OpenID Connect (OIDC)
	 * compliance using a customized specification.
	 *
	 * This method allows providing detailed customizations for OIDC-specific
	 * behaviors, enabling advanced configurations such as setting OIDC-specific
	 * request parameters or behaviors required for ID token handling
	 * and session management.
	 *
	 * @param dsl a {@code Consumer} that performs custom configurations on the {@link OidcRequestSpec}.
	 *            The consumer provides the flexibility to define OIDC-specific details
	 *            such as nonce values or any other required parameter.
	 * @return the current {@code AuthorizationCodeRequestSpec} instance for further configuration.
	 */
  AuthorizationCodeRequestSpec oidc(Consumer<OidcRequestSpec> dsl);

	/**
	 * Configures the Proof Key for Code Exchange (PKCE) for the authorization code request.
	 *
	 * This method enables PKCE, an OAuth 2.0 security feature that enhances the authorization
	 * code flow by mitigating certain attack vectors, such as authorization code interception.
	 * PKCE is particularly important for public clients that cannot securely store client secrets.
	 *
	 * @return the current {@code AuthorizationCodeRequestSpec} instance for further configuration
	 *         of the authorization code request.
	 */
  AuthorizationCodeRequestSpec pkce();

	/**
	 * Configures the Proof Key for Code Exchange (PKCE) parameters for the authorization code request.
	 * This method accepts a customization function to define the details of the PKCE configuration,
	 * such as setting the code verifier, which enhances security in the OAuth 2.0 authorization code flow.
	 *
	 * @param dsl a {@code Consumer} that allows customization of the {@link ProofKeyForCodeExchangeRequestSpec}.
	 *            The consumer is used to define the PKCE-specific parameters, providing details such as
	 *            the code verifier for the PKCE protocol.
	 * @return the current {@code AuthorizationCodeRequestSpec} instance for further configuration.
	 */
  AuthorizationCodeRequestSpec pkce(Consumer<ProofKeyForCodeExchangeRequestSpec> dsl);

	/**
	 * Configures the client identifier (client_id) for the authorization code request.
	 *
	 * This method allows setting the unique identifier that represents the client application
	 * in the context of the authorization process. It is a required parameter in an
	 * OAuth 2.0 or OpenID Connect (OIDC) request.
	 *
	 * @param state the client identifier to be configured
	 * @return the current {@code AuthorizationCodeRequestSpec} instance for further configuration
	 */
  AuthorizationCodeRequestSpec clientId(String state);

	/**
	 * Builds and returns the configured URI components for the authorization code request.
	 *
	 * This method finalizes the specification of the authorization code request
	 * and constructs the associated URI based on the configuration provided.
	 *
	 * @return the resulting {@code UriComponents} instance representing the constructed URI.
	 */
  UriComponents build();

	/**
	 * Represents a specification for configuring the Proof Key for Code Exchange (PKCE) parameters
	 * in an OAuth 2.0 authorization code request. This interface provides a method for setting
	 * the code verifier, a cryptographically random string required for enhancing security
	 * in authorization code flows.
	 */
  interface ProofKeyForCodeExchangeRequestSpec {
		/**
		 * Sets the code verifier for the PKCE (Proof Key for Code Exchange) protocol. The code verifier is
		 * a cryptographically random string used to securely bind the authorization code request to the
		 * token request during the OAuth 2.0 authorization flow.
		 *
		 * @param codeVerifier a non-empty, URL-safe string that serves as the code verifier for the PKCE
		 *                     protocol; it must match the requirements defined in the OAuth 2.0 PKCE
		 *                     specification
		 */
    void codeVerifier(String codeVerifier);
  }

	/**
	 * Represents a specification for OpenID Connect (OIDC) request configuration.
	 * This interface provides a fluent API for customizing the parameters associated
	 * with an OpenID Connect request, enabling features such as enhanced security
	 * and client-server interaction requirements.
	 */
  interface OidcRequestSpec {
		/**
		 * Sets the unique nonce value for the OpenID Connect (OIDC) request. The nonce is used to associate
		 * a client session with an ID token and to prevent replay attacks during the authentication process.
		 *
		 * @param nonce a unique string value to include in the OIDC request, typically generated for each
		 *              request to ensure security and integrity.
		 */
    void nonce(String nonce);
  }
}
