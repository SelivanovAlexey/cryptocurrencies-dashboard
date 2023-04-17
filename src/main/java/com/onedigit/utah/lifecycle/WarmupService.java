package com.onedigit.utah.lifecycle;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Connection;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WarmupService {

    final List<BaseExchangeAdapter> provider;

    public WarmupService(List<BaseExchangeAdapter> provider) {
        this.provider = provider;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doAfterStartup() {
        provider.stream()
                .filter(BaseExchangeAdapter::isEnabled)
                .forEach(adapter ->
                        adapter.watchMarketData()
                                .subscribe(response -> {
                                    adapter.storeMarketData(response);
                                    adapter.setConnectionStatus(Connection.ACTIVE);
                                })
                );
    }
}
