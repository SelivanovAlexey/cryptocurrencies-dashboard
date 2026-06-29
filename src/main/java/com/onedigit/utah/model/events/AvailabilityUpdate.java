package com.onedigit.utah.model.events;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailability;

import java.util.List;

public record AvailabilityUpdate(String ticker, Exchange exchange, List<NetworkAvailability> availability)
        implements UpdateEvent {}
