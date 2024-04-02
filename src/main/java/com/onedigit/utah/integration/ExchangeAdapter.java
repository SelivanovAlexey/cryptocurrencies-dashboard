package com.onedigit.utah.integration;

import com.onedigit.utah.model2.integration.common.RestResponse;
import reactor.core.publisher.Flux;

//TODO: refactor api regarding watching and storing market data. But if it is needed...
public interface ExchangeAdapter {

    Flux<? extends RestResponse> watchPrices();

    Flux<? extends RestResponse> watchAvailability();

    void populateSpreads(RestResponse response);

    void populateAvailability(RestResponse response);

    boolean isEnabled();
}
