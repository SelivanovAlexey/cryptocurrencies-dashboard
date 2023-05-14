package com.onedigit.utah.api;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.common.RestResponse;
import reactor.core.publisher.Flux;

//TODO: refactor api regarding watching and storing market data. But if it is needed...
public interface ExchangeAdapter {

    Flux<? extends RestResponse> watchPrices();

    void storePrices(RestResponse response);

    Boolean isEnabled();

    Exchange getExchangeName();

    Flux<? extends RestResponse> watchAvailability();

    void storeAvailability(RestResponse response);
}
