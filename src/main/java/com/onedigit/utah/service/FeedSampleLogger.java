package com.onedigit.utah.service;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.events.AvailabilityUpdate;
import com.onedigit.utah.model.events.PriceUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FeedSampleLogger {
    private final boolean enabled;

    public FeedSampleLogger(Environment env) {
        this.enabled = env.acceptsProfiles(Profiles.of("dev"));
    }

    public void prices(Exchange exchange, List<PriceUpdate> batch) {
        if (!enabled || batch.isEmpty()) return;
        PriceUpdate f = batch.getFirst(), l = batch.getLast();
        log.info("{} prices: {} tickers, first {}={}, last {}={}",
                exchange, batch.size(), f.ticker(), f.price(), l.ticker(), l.price());
    }

    public void availability(Exchange exchange, List<AvailabilityUpdate> batch) {
        if (!enabled || batch.isEmpty()) return;
        AvailabilityUpdate f = batch.getFirst(), l = batch.getLast();
        log.info("{} availability: {} tickers, first {}({} chains), last {}({} chains)",
                exchange, batch.size(), f.ticker(), f.availability().size(), l.ticker(), l.availability().size());
    }
}