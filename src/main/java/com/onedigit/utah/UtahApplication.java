package com.onedigit.utah;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Connection;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.common.RestResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Flux;

import java.util.Map;

@EnableScheduling
@SpringBootApplication
public class UtahApplication {

	final Map<Exchange, ExchangeAdapter> adapters;

	public UtahApplication(Map<Exchange, ExchangeAdapter> adapters) {
		this.adapters = adapters;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void retrievePrices() {
		adapters.forEach((exchange, adapter) ->
				adapter.watchPrices()
						.subscribe(response -> {
							adapter.storePrices(response);
							((BaseExchangeAdapter) adapter).setConnectionStatus(Connection.ACTIVE);
						})
		);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void retrieveAvailability() {
		adapters.forEach((exchange, adapter) -> {
					Flux<? extends RestResponse> m = adapter.watchAvailability();
					//TODO: null checking to remove when all availability calls will ready
					if (m != null) {
						m.subscribe(adapter::storeAvailability);
					}
				}
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(UtahApplication.class, args);
	}

}
