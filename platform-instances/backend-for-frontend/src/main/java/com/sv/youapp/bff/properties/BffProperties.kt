package com.sv.youapp.bff.properties

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("security.oauth2")
data class BffProperties(
    @field:NotBlank
    val URL: String,
    @field:NotBlank
    val redirectUri: String
    )
