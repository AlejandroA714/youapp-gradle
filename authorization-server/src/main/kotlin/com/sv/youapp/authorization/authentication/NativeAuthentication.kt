package com.sv.youapp.authorization.authentication

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken

val NATIVE_GRANT_TYPE = AuthorizationGrantType("urn:ietf:params:oauth:grant-type:native")

class NativeAuthentication(
    var username: String,
    val password: String,
    val clientPrincipal: OAuth2ClientAuthenticationToken,
    val state: String?,
    val scopes: Set<GrantedAuthority>
) : Authentication {
    var _authenticated = false

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return scopes
    }

    override fun getCredentials(): Any {
        return password
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any {
        return username
    }

    override fun isAuthenticated(): Boolean {
        return _authenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        _authenticated = isAuthenticated
    }
    // sub
    override fun getName(): String {
        return username
    }
}
