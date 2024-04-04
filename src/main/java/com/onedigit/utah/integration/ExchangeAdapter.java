package com.onedigit.utah.integration;

import com.onedigit.utah.model2.integration.common.RestResponse;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeAdapter {
    boolean isEnabled();

    Flux<? extends RestResponse> getAndPopulateSpreads();

    Flux<? extends RestResponse> getAndPopulateAvailability();

}
