package com.onedigit.utah.api.impl.ws;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import reactor.core.publisher.Mono;

/**
 * Implemented with WebSocket protocol
 */
public class BinanceAdapterImpl implements ExchangeAdapter {
    @Override
    public Mono<Void> getMarketData() {
        return null;
    }

    @Override
    public Boolean isEnabled() {
        return false;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.BINANCE;
    }
}
