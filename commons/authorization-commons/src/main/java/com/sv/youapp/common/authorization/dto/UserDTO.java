package com.sv.youapp.common.authorization.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Set;

@Getter
@ToString
@AllArgsConstructor
public class UserDTO implements UserDetails, CredentialsContainer {

    private final Integer id;
    private final String username;
    @Setter
    private String password;
    private final String email;
    private final String profilePictureUrl;
    private final Instant registeredAt;
    private final Set<GrantedAuthority> authorities;

    @JsonAlias("isEnabled")
    private final boolean enabled;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
