package com.onedigit.utah.api.common;

import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
public class RestApiAdapter {

    protected WebClient webClient;

    protected <T> Flux<T> getWithRepeat(String uriPath, Class<T> responseClass, Retry retrySpec) {
        return get(uriBuilder -> uriBuilder.path(uriPath).build(), responseClass)
                .repeat()
                .retryWhen(retrySpec);
    }

    protected <T> Flux<T> getWithRepeat(String uriPath, Map<String, List<String>> queryParams, Class<T> responseClass, Retry retrySpec) {
        return get(uriBuilder -> uriBuilder.path(uriPath).queryParams(CollectionUtils.toMultiValueMap(queryParams)).build(), responseClass)
                .repeat()
                .retryWhen(retrySpec);
    }

    protected <T> Mono<T> get(String uriPath, Class<T> responseClass) {
        return get(uriBuilder -> uriBuilder.path(uriPath).build(), responseClass);
    }

    protected <T> Mono<T> get(String uriPath, Map<String, List<String>> queryParams, Class<T> responseClass) {
        return get(uriBuilder -> uriBuilder.path(uriPath).queryParams(CollectionUtils.toMultiValueMap(queryParams)).build(), responseClass);
    }

    protected <T> Mono<T> get(URI uriPath, Class<T> responseClass) {
        return get(uriBuilder -> uriPath, responseClass);
    }

    protected <T> Mono<T> get(Function<UriBuilder, URI> uriBuilderFunction, Class<T> responseClass) {
        return exchange(HttpMethod.GET, uriBuilderFunction, responseClass);
    }

    protected <T> Mono<T> post(String uri, Class<T> responseClass) {
        return post(uriBuilder -> uriBuilder.path(uri).build(), responseClass);
    }

    protected <T> Mono<T> post(String uriPath, Map<String, List<String>> queryParams, Class<T> responseClass) {
        return post(uriBuilder -> uriBuilder.path(uriPath).queryParams(CollectionUtils.toMultiValueMap(queryParams)).build(), responseClass);
    }

    protected <T> Mono<T> post(URI uri, Class<T> responseClass) {
        return post(uriBuilder -> uri, responseClass);
    }

    protected <T> Mono<T> post(Function<UriBuilder, URI> uriBuilderFunction, Class<T> responseClass) {
        return exchange(HttpMethod.POST, uriBuilderFunction, responseClass);
    }

    private <T> Mono<T> exchange(HttpMethod method, Function<UriBuilder, URI> uriBuilderFunction, Class<T> responseClass) {
        return webClient
                .method(method)
                .uri(uriBuilderFunction)
                .retrieve()
                .bodyToMono(responseClass);
    }
}