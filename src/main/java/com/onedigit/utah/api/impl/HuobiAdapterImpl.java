package com.onedigit.utah.api.impl;

import com.onedigit.utah.api.ExchangeAdapter;
import reactor.core.publisher.Mono;

/**
 * Implemented with REST protocol
 */
public class HuobiAdapterImpl implements ExchangeAdapter {
    @Override
    public Mono<Void> getMarketData() {
        return null;
    }
}
