package com.onedigit.utah.integration;

import com.onedigit.utah.model2.api.SpreadDTO;
import com.onedigit.utah.model2.integration.common.RestResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ExchangeAdapter {
    boolean isEnabled();

    public Flux<List<List<SpreadDTO>>> getSpreadsFlux();

    Flux<? extends RestResponse> getAndPopulateAvailability();

}
