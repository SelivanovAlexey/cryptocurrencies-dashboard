package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import reactor.core.publisher.Mono;

/**
 * Implemented with REST protocol
 */
public class HuobiAdapterImpl extends ExchangeAdapter {
    private Boolean isConnectionActive;
    @Override
    public Mono<Void> getMarketData() {
        return null;
    }

    @Override
    public Boolean isEnabled() {
        return false;
    }

    @Override
    public Boolean isConnectionActive() {
        return isConnectionActive;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.HUOBI;
    }
}
