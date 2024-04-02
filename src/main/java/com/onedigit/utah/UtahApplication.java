package com.onedigit.utah;

import com.onedigit.utah.integration.ExchangeAdapter;
import com.onedigit.utah.lifecycle.LocalContextProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

@EnableScheduling
@SpringBootApplication
public class UtahApplication {

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        Map<String, ExchangeAdapter> adapters = LocalContextProvider.getContext().getBeansOfType(ExchangeAdapter.class);
        adapters.values().stream()
                .filter(ExchangeAdapter::isEnabled)
                .forEach(adapter -> {
//                            adapter.watchPrices().subscribe(adapter::populateSpreads);
                            adapter.watchAvailability().subscribe(adapter::populateAvailability);
                        }
                );
    }

//	@EventListener(ApplicationReadyEvent.class)
//	public void retrieveAvailability() {
//		adapters.forEach((exchange, adapter) -> {
//					Flux<? extends RestResponse> m = adapter.watchAvailability();
//					//TODO: null checking to remove when all availability calls will ready
//					if (m != null) {
//						m.subscribe(adapter::storeAvailability);
//					}
//				}
//		);
//	}

    public static void main(String[] args) {
        SpringApplication.run(UtahApplication.class, args);
    }

}
