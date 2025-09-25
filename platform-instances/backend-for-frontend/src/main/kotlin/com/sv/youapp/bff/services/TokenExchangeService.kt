package com.sv.youapp.bff.services

import reactor.core.publisher.Mono

interface TokenExchangeService {

    fun exchange(code: String): Mono<Map<*, *>?>;
}
