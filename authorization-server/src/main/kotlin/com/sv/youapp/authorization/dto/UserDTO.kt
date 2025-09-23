package com.sv.youapp.authorization.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

class  UserDTO(
    val id: Integer,
    private val username: String,
    private val password: String?,
    val email: String,
    val profilePictureUrl: String?,
    val registeredAt: Instant,
    private val authorities: Set<GrantedAuthority>,
    @JsonAlias("isEnabled")
    private val enabled: Boolean,
) : UserDetails {
    override fun isEnabled() = enabled

    override fun getUsername(): String = username

    override fun getPassword(): String? = password

    override fun getAuthorities() = authorities

    @JsonIgnore
    override fun isAccountNonExpired() = true

    @JsonIgnore
    override fun isAccountNonLocked() = true

    @JsonIgnore
    override fun isCredentialsNonExpired() = true
}
