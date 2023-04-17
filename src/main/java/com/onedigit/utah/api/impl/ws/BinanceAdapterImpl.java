package com.onedigit.utah.api.impl.ws;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.common.RestResponse;
import reactor.core.publisher.Flux;

/**
 * Implemented with WebSocket protocol
 */
public class BinanceAdapterImpl implements ExchangeAdapter {
    @Override
    public Flux<RestResponse> watchMarketData() {
        return null;
    }

    @Override
    public void storeMarketData(RestResponse response) {

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
