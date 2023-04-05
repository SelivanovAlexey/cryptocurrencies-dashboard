package com.onedigit.utah.api;

import com.onedigit.utah.model.Exchange;
import reactor.core.publisher.Mono;

public abstract class ExchangeAdapter {

    public abstract Mono<Void> getMarketData();

    public abstract Boolean isEnabled();

    public abstract Boolean isConnectionActive();

    public abstract Exchange getExchangeName();
}
