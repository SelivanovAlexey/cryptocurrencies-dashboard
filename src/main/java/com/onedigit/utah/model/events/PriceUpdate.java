package com.onedigit.utah.model.events;

import com.onedigit.utah.model.Exchange;

import java.math.BigDecimal;

public record PriceUpdate(String ticker, Exchange exchange, BigDecimal price)
        implements UpdateEvent {
}