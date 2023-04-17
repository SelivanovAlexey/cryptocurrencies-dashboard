package com.onedigit.utah.api;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.common.RestResponse;
import reactor.core.publisher.Flux;

public interface ExchangeAdapter {

    Flux<? extends RestResponse> watchMarketData();

    void storeMarketData(RestResponse response);

    Boolean isEnabled();

    Exchange getExchangeName();
}
