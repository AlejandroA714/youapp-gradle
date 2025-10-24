package com.sv.youapp.bff.services;

import reactor.core.publisher.Mono;
import java.util.Map;

public interface TokenExchangeService {
    Mono<Map> exchange(String code);
}
