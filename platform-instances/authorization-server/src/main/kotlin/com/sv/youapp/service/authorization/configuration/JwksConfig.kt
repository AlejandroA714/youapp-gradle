package com.sv.youapp.service.authorization.configuration

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.UUID

private const val AUTHORITIES = "authorities"

@Configuration
class JwksConfig {
    @Configuration
    class InMemoryConfiguration {
        @Bean
        fun userDetailsService(props: SecurityProperties): UserDetailsService {
            val u = props.user
            val user =
                User.withUsername(u.name)
                    .password(u.password)
                    .roles(*u.roles.toTypedArray())
                    .build()
            return InMemoryUserDetailsManager(user)
        }
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val rsaKey =
            RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }

    private fun generateRsaKey(): KeyPair {
        return try {
            val kpg = KeyPairGenerator.getInstance("RSA")
            kpg.initialize(2048)
            kpg.generateKeyPair()
        } catch (ex: Exception) {
            throw IllegalStateException(ex)
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    fun jwtTokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext?> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext? ->
            if (OAuth2TokenType.ACCESS_TOKEN == context!!.tokenType) {
                val principal: Authentication = context.getPrincipal()
                val authorities =
                    principal.authorities
                        ?.map { it.authority }
                        ?.toList()
                        ?: emptyList()
                context.claims.claims { claims ->
                    claims[AUTHORITIES] = authorities
                }
            }
        }
    }
}
