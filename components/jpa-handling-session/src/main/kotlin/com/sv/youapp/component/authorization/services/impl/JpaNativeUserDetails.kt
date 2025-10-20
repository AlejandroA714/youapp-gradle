package com.sv.youapp.component.authorization.services.impl

import com.sv.youapp.common.authorization.dto.UserDTO
import com.sv.youapp.common.authorization.services.NativeUserDetails
import com.sv.youapp.component.authorization.repositories.jpa.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JpaNativeUserDetails(
    private val repository:
    UserRepository,
) : NativeUserDetails {
    override fun loadUserByUsername(username: String): UserDetails {
        val u =
            repository.findAllByUsername(username)
                .orElseThrow { UsernameNotFoundException("User $username not found") }
        val roleAuthorities = u.roles.map { SimpleGrantedAuthority(it.name) }
        val directAuthorities = u.authorities.map { SimpleGrantedAuthority(it.name) }
        val roleBasedAuthorities = u.roles.flatMap { role -> role.authorities.map { SimpleGrantedAuthority(it.name) } }
        val allAuthorities =
            (roleAuthorities + directAuthorities + roleBasedAuthorities)
                .distinctBy { it.authority }.toSet()
        return UserDTO(
            u.id,
            u.username,
            u.password,
            u.email,
            u.profilePictureUrl,
            u.registeredAt,
            allAuthorities,
            u.enabled,
        )
    }
}
