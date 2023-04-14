package com.onedigit.utah.lifecycle;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HealthCheckService {
    final List<BaseExchangeAdapter> activeProviders;

    public HealthCheckService(List<BaseExchangeAdapter> providers) {
        this.activeProviders = providers.stream().filter(ExchangeAdapter::isEnabled).collect(Collectors.toList());
    }

    @Scheduled(fixedRateString = "#{T(com.onedigit.utah.constants.ApiConstants).HEALTHCHECK_INTERVAL_MS}")
    public void checkHealth() {
        activeProviders.
                forEach(prov ->
                        log.info("Healthcheck :: exchange {}, connection :: {}", prov.getExchangeName(), prov.getConnectionStatus())
                );
    }
}
