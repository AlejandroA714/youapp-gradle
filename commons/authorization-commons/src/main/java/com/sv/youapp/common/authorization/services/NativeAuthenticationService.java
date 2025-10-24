package com.sv.youapp.common.authorization.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface NativeAuthenticationService {
    UserDetails authenticate(Authentication authentication);
}
