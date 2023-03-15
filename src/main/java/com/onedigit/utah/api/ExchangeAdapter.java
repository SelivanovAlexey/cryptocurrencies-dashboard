package com.onedigit.utah.api;

import reactor.core.publisher.Mono;

public interface ExchangeAdapter {
    Mono<Void> getMarketData() throws Exception;
}
