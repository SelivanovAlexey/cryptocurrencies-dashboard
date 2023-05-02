package com.onedigit.utah.api;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailabilityDTO;
import com.onedigit.utah.model.api.common.RestResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

//TODO: refactor api regarding watching and storing market data. But if it is needed...
public interface ExchangeAdapter {

    Flux<? extends RestResponse> watchMarketData();

    void storeMarketData(RestResponse response);

    Boolean isEnabled();

    Exchange getExchangeName();

    Mono<List<NetworkAvailabilityDTO>> isWithdrawAvailable(String ticker);

    Mono<List<NetworkAvailabilityDTO>> isDepositAvailable(String ticker);
}
