package com.sv.youapp.component.authorization.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Set;

public abstract class UserDTOMixin {

    @JsonProperty("id")
    Integer id;

    @JsonProperty("username")
    String username;

    @JsonProperty("password")
    String password;

    @JsonProperty("email")
    String email;

    @JsonProperty("profilePictureUrl")
    String profilePictureUrl;

    @JsonProperty("registeredAt")
    Instant registeredAt;

    @JsonProperty("authorities")
    Set<GrantedAuthority> authorities;

    @JsonProperty("isEnabled")
    Boolean enabled;
}
