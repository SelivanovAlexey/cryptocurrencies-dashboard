package com.onedigit.utah;

import com.onedigit.utah.api.FluxFacade;
import com.onedigit.utah.integration.ExchangeAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;

@EnableScheduling
@SpringBootApplication
public class UtahApplication {

    private final FluxFacade fluxFacade;

    public UtahApplication(FluxFacade fluxFacade) {
        this.fluxFacade = fluxFacade;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        fluxFacade.getSpreadsFlux().subscribe();
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
