package com.onedigit.utah.integration.common;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RestErrorLoggingFilter implements ExchangeFilterFunction {

    @Override
    @NullMarked
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request).flatMap(response -> {
            if (!response.statusCode().isError()) return Mono.just(response);
            return response.bodyToMono(String.class).defaultIfEmpty("")
                    .flatMap(body -> {
                        log.warn("HTTP {} {} | headers={} | body={}",
                                response.statusCode().value(), request.url(),
                                response.headers().asHttpHeaders(), body);
                        return Mono.just(response.mutate().body(body).build());
                    });
        });
    }
}
