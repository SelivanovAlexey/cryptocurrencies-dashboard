package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailabilityDTO;
import com.onedigit.utah.model.api.common.RestResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @Override
    public Mono<List<NetworkAvailabilityDTO>> isWithdrawAvailable(String ticker) {
        return null;
    }

    @Override
    public Mono<List<NetworkAvailabilityDTO>> isDepositAvailable(String ticker) {
        return null;
    }
}
