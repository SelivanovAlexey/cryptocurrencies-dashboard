package com.onedigit.utah.api;

import com.onedigit.utah.integration.ExchangeAdapter;
import com.onedigit.utah.lifecycle.LocalContextProvider;
import com.onedigit.utah.model2.api.SpreadDTO;
import lombok.Getter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class FluxFacade {
    @Getter
    private final Flux<List<List<SpreadDTO>>> spreadsFlux;
//    private final Flux<List<NetworkAvailabilityDTO>> chainsFlux;

    public FluxFacade(LocalContextProvider lcProvider) {
        spreadsFlux = Flux.fromStream(
                        lcProvider.getContext().getBeansOfType(ExchangeAdapter.class).values().stream())
                .flatMap(ExchangeAdapter::getSpreadsFlux)
                .filter(list -> !list.isEmpty())
                .share();
//        chainsFlux = Flux.fromStream(
//                        lcProvider.getContext().getBeansOfType(ExchangeAdapter.class).values().stream())
//                .flatMap(ExchangeAdapter::getSpreadsFlux).share();
    }
}
