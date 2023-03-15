package com.onedigit.utah.api.impl;

import com.onedigit.utah.api.ExchangeAdapter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BybitAdapterImpl implements ExchangeAdapter {
    @Override
    public Mono<Void> getMarketData() {
        return Mono.empty();
    }
}
