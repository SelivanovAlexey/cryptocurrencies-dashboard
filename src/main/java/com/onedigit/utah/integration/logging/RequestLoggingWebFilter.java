package com.onedigit.utah.integration.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//TODO: logging without body. Do i really need the body here?
@Component
@Slf4j
public class RequestLoggingWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (log.isDebugEnabled()) {
            ServerHttpRequest request = exchange.getRequest();
            StringBuilder sb = new StringBuilder("Request:\n")
                    .append(request.getMethod())
                    .append(request.getURI());
            log.debug(sb.toString());
        }
        return chain.filter(exchange);
    }
}
