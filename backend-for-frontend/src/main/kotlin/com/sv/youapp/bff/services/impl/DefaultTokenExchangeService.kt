package com.sv.youapp.bff.services.impl

import com.sv.youapp.bff.services.TokenExchangeService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class DefaultTokenExchangeService(private val webClient: WebClient): TokenExchangeService {
    override fun exchange(code: String): Mono<Map<*, *>?> {
       return webClient.post().uri("/oauth2/token")
            .headers { x: HttpHeaders ->  x.setBasicAuth("oidc-client", "secret") }
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body (
                BodyInserters.fromFormData("grant_type", "authorization_code")
                    .with("code", code)
                    .with("redirect_uri", "http://192.168.1.24:8081/oauth2/callback")
            ).retrieve()
            .bodyToMono(Map::class.java)
    }
}

