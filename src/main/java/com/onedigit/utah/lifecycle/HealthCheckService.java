package com.onedigit.utah.lifecycle;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class HealthCheckService {
    final Map<Exchange, ExchangeAdapter> activeAdapters;

    public HealthCheckService(Map<Exchange, ExchangeAdapter> activeAdapters) {
        this.activeAdapters = activeAdapters;
    }

    @Scheduled(fixedRateString = "#{T(com.onedigit.utah.constants.ApiConstants).HEALTHCHECK_INTERVAL_MS}")
    public void checkHealth() {
        activeAdapters.
                forEach((exchange, adapter) ->
                        log.info("Healthcheck :: exchange {}, connection :: {}",
                                adapter.getExchangeName(),
                                ((BaseExchangeAdapter) adapter).getConnectionStatus())
                );
    }
}
