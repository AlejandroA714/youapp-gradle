package com.sv.youapp.authorization.services.impl

import com.sv.youapp.authorization.services.AuthenticationService
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes

class DefaultAuthenticationService(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationService {
    override fun authenticate(authentication: Authentication): UserDetails {
        val userDetails: UserDetails? =
            try {
                userDetailsService.loadUserByUsername(authentication.name)
            } catch (_: UsernameNotFoundException) {
                null
            }
        val matches: Boolean =
            userDetails?.let {
                passwordEncoder.matches(authentication.credentials as String, it.password)
            } ?: false
        if (!matches) {
            throw OAuth2AuthenticationException(
                OAuth2Error(
                    OAuth2ErrorCodes.INVALID_GRANT,
                    "Invalid credentials provided. Please check your username and password.",
                    null,
                ),
            )
        }
        return userDetails
    }
}
