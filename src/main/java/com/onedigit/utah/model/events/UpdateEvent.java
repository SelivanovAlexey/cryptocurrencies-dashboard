package com.onedigit.utah.model.events;

import com.onedigit.utah.model.Exchange;

public sealed interface UpdateEvent
        permits PriceUpdate, AvailabilityUpdate {
    String ticker();
    Exchange exchange();
}