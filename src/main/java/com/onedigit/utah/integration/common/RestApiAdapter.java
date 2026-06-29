package com.onedigit.utah.integration.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class RestApiAdapter {

    protected WebClient webClient;

    protected <T> Flux<T> getWithRepeat(Consumer<UriBuilder> uriConsumer,
                                        Class<T> responseClass,
                                        Duration delay, Retry retrySpec) {
        return getWithRepeat(uriConsumer, httpHeaders -> {}, responseClass, delay, retrySpec);
    }

    protected <T> Flux<T> getWithRepeat(Consumer<UriBuilder> uriConsumer,
                                        Consumer<HttpHeaders> headersConsumer,
                                        Class<T> responseClass,
                                        Duration delay, Retry retrySpec) {
        return get(uriBuilder -> {
            uriConsumer.accept(uriBuilder);
            return uriBuilder.build();
        }, headersConsumer, responseClass)
                .repeatWhen(completed -> completed.delayElements(delay))
                .retryWhen(retrySpec)
                .elapsed()
                .doOnNext(t -> log.info("cycle {} {} ms", responseClass.getSimpleName(), t.getT1()))
                .map(Tuple2::getT2);
    }

    private <T> Mono<T> get(Function<UriBuilder, URI> uriBuilderFunction, Consumer<HttpHeaders> headersConsumer, Class<T> responseClass) {
        return exchange(HttpMethod.GET, uriBuilderFunction, headersConsumer, responseClass);
    }

    private <T> Mono<T> exchange(HttpMethod method, Function<UriBuilder, URI> uriBuilderFunction,
                                 Consumer<HttpHeaders> headersConsumer, Class<T> responseClass) {
        return Mono.defer(() -> {
            long t0 = System.nanoTime();
            return webClient
                    .method(method)
                    .uri(uriBuilderFunction)
                    .headers(headersConsumer)
                    .retrieve()
                    .bodyToMono(responseClass)
                    .doOnNext(r -> log.info("http {} {} ms", responseClass.getSimpleName(), (System.nanoTime() - t0) / 1_000_000));
        });
    }
}