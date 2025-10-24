//package com.sv.youapp.bff.services.impl
//
//import com.sv.youapp.bff.services.TokenExchangeService
//import org.springframework.http.HttpHeaders
//import org.springframework.http.MediaType
//import org.springframework.security.config.Customizer
//import org.springframework.web.reactive.function.BodyInserters
//import org.springframework.web.reactive.function.client.WebClient
//import reactor.core.publisher.Mono
//
//class DefaultTokenExchangeService private constructor(val uri: String,
//                                                      val redirectUri: String, val webClient: WebClient): TokenExchangeService {
//
//    companion object {
//        fun builder(): Builder = Builder()
//    }
//
//    class Builder {
//        private var uri: String = "/oauth2/token"
//
//        private var redirectUri: String = "/oauth2/callback"
//
//        private var webClient: TokenWebClientSpec = TokenWebClientSpec()
//
//        fun tokenUri(uri: String) =  apply { this.uri = uri }
//
//        fun redirectUri(url: String) = apply { this.redirectUri = url }
//
//        fun client(customizer: Customizer<TokenWebClientSpec>) = apply { customizer.customize(this.webClient) }
//
//        //fun client(webClient: WebClient) = apply { this.webClient = webClient }
//
//        fun build(): TokenExchangeService {
//            return DefaultTokenExchangeService(uri, redirectUri, webClient.configure())
//        }
//    }
//
//    class TokenWebClientSpec {
//        private var host = "http://authentication-server/"
//
//        fun host(host: String) = apply { this.host = host }
//
//        internal fun configure(): WebClient {
//            return WebClient.builder().baseUrl(host).build()
//        }
//    }
//
//
//
//
//    override fun exchange(code: String): Mono<Map<*, *>?> {
//        return webClient.post().uri(uri)
//            .headers { x: HttpHeaders ->  x.setBasicAuth("oidc-client", "secret") }
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//            .body (
//                BodyInserters.fromFormData("grant_type", "authorization_code")
//                    .with("code", code)
//                    .with("redirect_uri", "http://192.168.1.24:8083/oauth2/callback")
//            ).retrieve()
//            .bodyToMono(Map::class.java)
//    }
//
//    override fun init(): Mono<Void> {
//        TODO("Not yet implemented")
//    }
//
//
//}
//
//

