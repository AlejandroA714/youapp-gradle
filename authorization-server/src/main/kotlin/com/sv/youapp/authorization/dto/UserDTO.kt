package com.sv.youapp.authorization.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

class UserDTO(
    val id: Integer,
    private val user: String,
    private val pass: String,
    val email: String,
    val profilePictureUrl: String?,
    val registeredAt: Instant,
    private val granted: Set<GrantedAuthority>,
    private val enabled: Boolean,
) : UserDetails {
    override fun isEnabled() = enabled

    override fun getUsername(): String = user

    override fun getPassword(): String = pass

    override fun getAuthorities() = granted

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true
}
