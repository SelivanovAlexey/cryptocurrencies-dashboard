package com.onedigit.utah.service.event.publishers;

import com.onedigit.utah.model.api.CoinView;
import reactor.core.publisher.Flux;

public abstract class CoinViewPublisher extends Publisher<CoinView> {

    public Flux<CoinView> spreads() {
        return sink.asFlux()
                .filter(v -> !v.spreads().isEmpty())
                .map(v -> new CoinView(v.ticker(), v.spreads()));
    }

    public Flux<CoinView> detail(String ticker) {
        return sink.asFlux().filter(v -> v.ticker().equals(ticker));
    }
}
