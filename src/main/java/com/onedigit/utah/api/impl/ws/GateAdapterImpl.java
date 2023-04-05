package com.onedigit.utah.api.impl.ws;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import reactor.core.publisher.Mono;

/**
 * Implemented with WebSocket protocol
 */
public class GateAdapterImpl extends ExchangeAdapter {
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
        return null;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.GATE;
    }

}
