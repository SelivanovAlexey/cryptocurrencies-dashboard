package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.common.RestResponse;
import reactor.core.publisher.Flux;

public class HuobiAdapterImpl extends BaseExchangeAdapter {
    @Override
    public Flux<? extends RestResponse> watchMarketData() {
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
        return Exchange.HUOBI;
    }
}
