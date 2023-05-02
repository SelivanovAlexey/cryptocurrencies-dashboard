package com.onedigit.utah.lifecycle;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Connection;
import com.onedigit.utah.model.Exchange;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WarmupService {

    final Map<Exchange, ExchangeAdapter> adapters;

    public WarmupService(Map<Exchange, ExchangeAdapter> adapters) {
        this.adapters = adapters;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doAfterStartup() {
        adapters.forEach((exchange, adapter) ->
                adapter.watchMarketData()
                        .subscribe(response -> {
                            adapter.storeMarketData(response);
                            ((BaseExchangeAdapter) adapter).setConnectionStatus(Connection.ACTIVE);
                        })
        );
    }
}
