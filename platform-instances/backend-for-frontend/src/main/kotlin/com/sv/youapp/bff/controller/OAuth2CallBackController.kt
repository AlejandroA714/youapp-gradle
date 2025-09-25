package com.sv.youapp.bff.controller

import com.sv.youapp.bff.services.TokenExchangeService
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/oauth2")
class OAuth2CallBackController(private val tokenExchangeService: TokenExchangeService) {

    @GetMapping("/callback")
    fun callback( @RequestParam("code") code: String,
                  @RequestParam("state") state: String,
                  res: ServerHttpResponse): Mono<Void> {
        return tokenExchangeService.exchange(code).flatMap {
            println("HHHH")
            redirect(res, "youapp://oauth2?sid=$code")
        }
    }

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.FOUND)
    fun login(res: ServerHttpResponse): Mono<Void>{
        val q = listOf(
            "response_type=code",
            "client_id=oidc-client",
            "redirect_uri=http://192.168.1.24:8083/oauth2/callback",
            "scope=profile",
            "state=asd123sad" // ← state = sid generado por el BFF
        ).joinToString("&")
        return redirect(res,"http://192.168.1.24:8082/oauth2/authorize?$q")
    }

    private fun redirect(
        res: ServerHttpResponse,
        url: String,
    ): Mono<Void> {
        res.statusCode = HttpStatus.FOUND
        res.headers.location = java.net.URI.create(url)
        res.headers.add("Cache-Control", "no-store")
        return res.setComplete()
    }


}
