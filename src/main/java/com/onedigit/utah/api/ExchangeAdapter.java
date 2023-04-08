package com.onedigit.utah.api;

import com.onedigit.utah.model.Exchange;
import reactor.core.publisher.Mono;

//TODO: refactor API error handling
public interface ExchangeAdapter {

    Mono<Void> getMarketData();

    Boolean isEnabled();

    Exchange getExchangeName();
}
