package com.sv.youapp.component.authorization.services.impl;

import com.sv.youapp.common.authorization.services.NativeAuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

public class DefaultNativeAuthenticationService implements NativeAuthenticationService {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public DefaultNativeAuthenticationService(UserDetailsService userDetailsService,
    PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails authenticate(Authentication authentication) {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        } catch (UsernameNotFoundException ex) {
            throw authenticationException();
        }

        boolean matches = userDetails != null
            && passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userDetails.getPassword());

        if (!matches) {
            throw authenticationException();
        }
        return userDetails;
    }

    private OAuth2AuthenticationException authenticationException() {
        return new OAuth2AuthenticationException(
                new OAuth2Error(
                        OAuth2ErrorCodes.INVALID_GRANT,
        "Invalid credentials provided. Please check your username and password.",
        null
        )
        );
    }
}
