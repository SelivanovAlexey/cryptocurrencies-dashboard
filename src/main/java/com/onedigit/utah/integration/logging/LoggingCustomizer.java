package com.onedigit.utah.integration.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
//TODO: think about correct representation of logPrefix
//TODO: log body on trace/debug
public class LoggingCustomizer implements WebClientCustomizer {


    @Override
    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.filter(logRequest());
        webClientBuilder.filter(logResponse());
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("\n==== outgoing integration request ====\n")
                        .append("correlation id: ")
                        .append(clientRequest.logPrefix())
                        .append("\n")
                        .append(clientRequest.method())
                        .append(" ")
                        .append(clientRequest.url())
                        .append("\n======================================\n");
                log.debug(sb.toString());
            }
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("\n==== incoming integration response ====\n")
                        .append("correlation id: ")
                        .append(clientResponse.logPrefix())
                        .append("\n")
                        .append(clientResponse.statusCode())
                        .append("\n======================================\n");
                log.debug(sb.toString());
            }
            return Mono.just(clientResponse);
        });
    }


}
