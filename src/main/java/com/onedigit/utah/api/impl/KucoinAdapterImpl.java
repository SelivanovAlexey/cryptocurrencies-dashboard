package com.onedigit.utah.api.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.kucoin.rest.KucoinRestData;
import com.onedigit.utah.model.api.kucoin.rest.KucoinRestInstanceServer;
import com.onedigit.utah.model.api.kucoin.rest.KucoinRestResponse;
import com.onedigit.utah.model.api.kucoin.ws.KucoinWsMessage;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import static com.onedigit.utah.constants.ApiConstants.*;

/**
 * Implemented with WebSocket protocol
 */
@Slf4j
@Service
public class KucoinAdapterImpl implements ExchangeAdapter {
    private final WebSocketClient webSocketClient;
    private final WebClient kucoinRestApiClient;
    private final ObjectMapper mapper;

    public KucoinAdapterImpl(@Qualifier("webSocketClient") WebSocketClient webSocketClient,
                             @Qualifier("kucoinRestApiClient") WebClient kucoinRestApiClient, ObjectMapper mapper) {
        this.webSocketClient = webSocketClient;
        this.kucoinRestApiClient = kucoinRestApiClient;
        this.mapper = mapper;
    }

    //TODO: make first rest call to get all market data and ONLY after that create a ws connection
    @Override
    @SneakyThrows
    public Mono<Void> getMarketData() {
        log.debug("Started getMarkedData from kucoin ");
        KucoinRestResponse tokenResponse = getConnectToken();
        String endpoint = getEndpointFromConnectTokenResponse(tokenResponse);
        String token = getTokenFromConnectTokenResponse(tokenResponse);
        Integer pingInterval = getPingIntervalFromConnectTokenResponse(tokenResponse);
        log.debug("getConnectToken info: {}", tokenResponse);

        //TODO: to refactor with handle method
        return webSocketClient.execute(
                URI.create(endpoint + "?token=" + token),
                session -> {
                    Mono<Void> mainFlow = session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .map(payload -> {
                                //TODO: to replace with general error handling
                                try {
                                    return mapper.readValue(payload, KucoinWsMessage.class);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .flatMap(message -> {
                                if (message.getType().equals("welcome")) {
                                    log.debug("Received welcome message: {}", message.asJsonString(mapper));
                                    return session.send(Mono.just(session.textMessage(buildSubscribeMessage(KUCOIN_TOPIC_MARKET_DATA))));
                                }
                                if (message.getType().equals("message")) {
//                                    log.debug("Received price message: {}", message.asJsonString(mapper));
                                    if (message.getSubject().endsWith("USDT")) {
                                        String ticker = StringUtils.substringBefore(message.getSubject(), "-USDT");
                                        BigDecimal price = new BigDecimal(message.getData().getPrice());

                                        MarketLocalCache.getTickerInfo(ticker).getPriceToExchange().put(Exchange.KUCOIN, price);

                                    }
                                    return session.send(Mono.empty());
                                }
                                if (message.getType().equals("pong")) {
                                    log.info("Received pong: {}", message.asJsonString(mapper));
                                    return session.send(Mono.empty());
                                }
                                return session.send(Mono.empty());
                            })
                            .then();

                    String pingMessage = KucoinWsMessage.builder()
                            .type("ping")
                            .build().asJsonString(mapper);

                    Mono<Void> pingFlow = session.send(Flux.interval(Duration.ofMillis(pingInterval)).flatMap(interval -> {
                        log.info("Send ping: {}", pingMessage);
                        return Mono.just(session.textMessage(pingMessage));
                    }));

                    return Mono.zip(mainFlow, pingFlow).then();
                });
        //TODO: is there is needed a session killer ?
    }

    private KucoinRestResponse getConnectToken() {
        return kucoinRestApiClient
                .post()
                .uri(URI.create(KUCOIN_API_REST_BASE_URL + KUCOIN_API_REST_GET_CONNECT_TOKEN_URL))
                .retrieve().bodyToMono(KucoinRestResponse.class).block();
    }

    private String getEndpointFromConnectTokenResponse(KucoinRestResponse response) throws Exception {
        return Optional.ofNullable(response)
                .map(KucoinRestResponse::getData)
                .map(KucoinRestData::getInstanceServers)
                .flatMap(list -> list.stream().findFirst())
                .map(KucoinRestInstanceServer::getEndpoint)
                .orElseThrow(() -> new Exception("No endpoint was received from Connect Token response"));
    }

    private String getTokenFromConnectTokenResponse(KucoinRestResponse response) throws Exception {
        return Optional.ofNullable(response)
                .map(KucoinRestResponse::getData)
                .map(KucoinRestData::getToken)
                .orElseThrow(() -> new Exception("No token was received from Connect Token response"));
    }

    private Integer getPingIntervalFromConnectTokenResponse(KucoinRestResponse tokenResponse) throws Exception {
        return Optional.ofNullable(tokenResponse)
                .map(KucoinRestResponse::getData)
                .map(KucoinRestData::getInstanceServers)
                .flatMap(list -> list.stream().findFirst())
                .map(KucoinRestInstanceServer::getPingInterval)
                .orElseThrow(() -> new Exception("No endpoint was received from Connect Token response"));
    }

    private String buildSubscribeMessage(String topic) {
        return
                KucoinWsMessage.builder()
                        .topic(topic)
                        .type("subscribe")
                        .build().asJsonString(mapper);
    }
}


