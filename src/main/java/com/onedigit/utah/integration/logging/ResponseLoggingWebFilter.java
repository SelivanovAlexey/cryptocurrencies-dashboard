package com.onedigit.utah.integration.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ResponseLoggingWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (log.isDebugEnabled()) {
            ServerHttpResponse response = exchange.getResponse();
            StringBuilder sb = new StringBuilder("Response:\n")
                    .append(response.getStatusCode());
            log.debug(sb.toString());
        }
        return chain.filter(exchange);
    }
}
