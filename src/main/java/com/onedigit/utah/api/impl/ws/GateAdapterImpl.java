package com.onedigit.utah.api.impl.ws;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;

/**
 * Implemented with WebSocket protocol
 */
public class GateAdapterImpl implements ExchangeAdapter {
    @Override
    public void getMarketData() {
    }

    @Override
    public Boolean isEnabled() {
        return false;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.GATE;
    }

}
