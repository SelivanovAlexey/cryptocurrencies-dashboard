package com.onedigit.utah.api.impl.ws;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.common.RestResponse;
import reactor.core.publisher.Flux;

public class GateAdapterImpl implements ExchangeAdapter {
    @Override
    public Flux<RestResponse> watchPrices() {
        return null;
    }

    @Override
    public void storePrices(RestResponse response) {

    }

    @Override
    public Boolean isEnabled() {
        return false;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.GATE;
    }

    @Override
    public Flux<? extends RestResponse> watchAvailability() {
        return null;
    }

    @Override
    public void storeAvailability(RestResponse response) {

    }

}
