package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.common.RestResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public class HuobiAdapterImpl extends BaseExchangeAdapter {
    @Override
    public Flux<? extends RestResponse> watchPrices() {
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
        return Exchange.HUOBI;
    }

    @Override
    public Flux<? extends RestResponse> watchAvailability() {
        return null;
    }

    @Override
    public void storeAvailability(RestResponse response) {

    }
}
