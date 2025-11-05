package com.sv.youapp.bff.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "security.oauth2")
public record BackEndForFrontEndProperties(
    @NotNull URI url, @NotBlank String clientId, @NotBlank String clientSecret) {}
