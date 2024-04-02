package com.onedigit.utah.integration.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class RestApiAdapter {

    protected WebClient webClient;

    protected <T> Flux<T> getWithDelayedRepeat(String uriPath, Consumer<MultiValueMap<String, String>> queryParamsConsumer, Consumer<HttpHeaders> headersConsumer, Class<T> responseClass, Duration delay, Retry retrySpec) {
        return get(
                uriBuilder -> uriBuilder.path(uriPath).queryParams(buildParamsFromConsumer(queryParamsConsumer)).build(), headersConsumer, responseClass)
                .repeat()
                .delayElements(delay)
                .retryWhen(retrySpec);
    }

    protected <T> Flux<T> getWithDelayedRepeat(String uriPath, Map<String, List<String>> queryParams, Class<T> responseClass, Duration delay, Retry retrySpec) {
        return get(uriBuilder -> uriBuilder.path(uriPath).queryParams(CollectionUtils.toMultiValueMap(queryParams)).build(), responseClass)
                .repeat()
                .delayElements(delay)
                .retryWhen(retrySpec);
    }

    protected <T> Flux<T> getWithDelayedRepeat(String uriPath, Class<T> responseClass, Duration delay, Retry retrySpec) {
        return get(uriBuilder -> uriBuilder.path(uriPath).build(), responseClass)
                .repeat()
                .delayElements(delay)
                .retryWhen(retrySpec);
    }

    protected <T> Mono<T> get(Function<UriBuilder, URI> uriBuilderFunction, Class<T> responseClass) {
        return exchange(HttpMethod.GET, uriBuilderFunction, responseClass);
    }

    protected <T> Mono<T> get(Function<UriBuilder, URI> uriBuilderFunction, Consumer<HttpHeaders> headersConsumer, Class<T> responseClass) {
        return exchange(HttpMethod.GET, uriBuilderFunction, headersConsumer, responseClass);
    }

    private <T> Mono<T> exchange(HttpMethod method, Function<UriBuilder, URI> uriBuilderFunction,
                                 Consumer<HttpHeaders> headersConsumer, Class<T> responseClass) {
        return Mono.defer(() -> webClient
                .method(method)
                .uri(uriBuilderFunction)
                .headers(headersConsumer)
                .retrieve()
                .bodyToMono(responseClass));
    }

    private <T> Mono<T> exchange(HttpMethod method, Function<UriBuilder, URI> uriBuilderFunction, Class<T> responseClass) {
        return Mono.defer(() -> webClient
                .method(method)
                .uri(uriBuilderFunction)
                .retrieve()
                .bodyToMono(responseClass));
    }

    private <K, V> MultiValueMap<K, V> buildParamsFromConsumer(Consumer<MultiValueMap<K, V>> paramsConsumer) {
        MultiValueMap<K, V> params = CollectionUtils.toMultiValueMap(new HashMap<>());
        paramsConsumer.accept(params);
        return params;
    }
}