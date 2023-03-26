package com.onedigit.utah.lifecycle;

import com.onedigit.utah.api.ExchangeAdapter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
@Component
public class WarmupService {

    final List<ExchangeAdapter> provider;

    public WarmupService(List<ExchangeAdapter> provider) {
        this.provider = provider;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doAfterStartup() {
        provider.stream()
//                .filter(adapter -> adapter.getClass().equals(KucoinAdapterImpl.class) || adapter.getClass().equals(BybitAdapterImpl.class))
                .map(ExchangeAdapter::getMarketData)
                .forEach(Mono::subscribe);
    }
}
