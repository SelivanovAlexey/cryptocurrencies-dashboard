package com.onedigit.utah.integration;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.events.AvailabilityUpdate;
import com.onedigit.utah.model.events.PriceUpdate;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ExchangeAdapter {
    Exchange exchange();
    Flux<List<PriceUpdate>> priceUpdates();
    Flux<List<AvailabilityUpdate>> availabilityUpdates();
}
