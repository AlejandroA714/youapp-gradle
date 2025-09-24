package com.sv.youapp.authorization.converter

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.core.GrantedAuthority
import java.time.Instant

abstract class UserDTOMixin(
    @JsonProperty("id") val id: Int,
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("profilePictureUrl") val profilePictureUrl: String?,
    @JsonProperty("registeredAt") val registeredAt: Instant,
    @JsonProperty("authorities") val authorities: Set<GrantedAuthority>,
    @JsonProperty("isEnabled") val enabled: Boolean,
)
