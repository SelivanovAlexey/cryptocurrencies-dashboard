package com.onedigit.utah.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.kucoin.JsonKucoinResponseDTO;
import com.onedigit.utah.model.api.kucoin.KucoinDataDTO;
import com.onedigit.utah.model.api.kucoin.KucoinInstanceServerDTO;
import com.onedigit.utah.model.api.kucoin.messages.KucoinWsMessage;
import com.onedigit.utah.rest.api.ExchangeAdapter;
import com.onedigit.utah.service.MarketLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.*;

import static com.onedigit.utah.constants.ApiConstants.*;

@Slf4j
@Service
public class KucoinAdapterImpl implements ExchangeAdapter {
    private final WebSocketClient kucoinWsApiClient;
    private final WebClient kucoinRestApiClient;
    private final ObjectMapper mapper;

    public KucoinAdapterImpl(@Qualifier("kucoinWsApiClient") WebSocketClient kucoinWebClient, @Qualifier("kucoinApiClient") WebClient kucoinRestApiClient, ObjectMapper mapper) {
        this.kucoinWsApiClient = kucoinWebClient;
        this.kucoinRestApiClient = kucoinRestApiClient;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> getMarketData() throws Exception {
        log.debug("Started getMarkedData");
        JsonKucoinResponseDTO tokenResponse = getConnectToken();
        String endpoint = getEndpointFromConnectTokenResponse(tokenResponse);
        String token = getTokenFromConnectTokenResponse(tokenResponse);
        Integer pingInterval = getPingIntervalFromConnectTokenResponse(tokenResponse);
        log.debug("getConnectToken info: {}", tokenResponse);

        return kucoinWsApiClient.execute(
                URI.create(endpoint + "?token=" + token),
                session -> {
                    Mono<Void> mainFlow = session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .map(payload -> {
                                //TODO: replace with general error handling
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
                                    log.debug("Received price message: {}", message.asJsonString(mapper));
                                    if (message.getSubject().endsWith("USDT")) {
                                        String ticker = StringUtils.substringBefore(message.getSubject(), "-USDT");
                                        Double price = Double.valueOf(message.getData().getPrice());
                                        MarketLocalCache.getAllExchangesData().get(Exchange.KUCOIN)
                                                .put(ticker, price);
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

                    return Mono.zip(mainFlow, pingFlow).then().log();
                });
        //TODO: is there is needed a session killer ?
    }

    private JsonKucoinResponseDTO getConnectToken() {
        return kucoinRestApiClient
                .post()
                .uri(URI.create(KUCOIN_API_BASE_URL + KUCOIN_API_GET_CONNECT_TOKEN_URL))
                .retrieve().bodyToMono(JsonKucoinResponseDTO.class).block();
    }

    private String getEndpointFromConnectTokenResponse(JsonKucoinResponseDTO response) throws Exception {
        return Optional.ofNullable(response)
                .map(JsonKucoinResponseDTO::getData)
                .map(KucoinDataDTO::getInstanceServers)
                .flatMap(list -> list.stream().findFirst())
                .map(KucoinInstanceServerDTO::getEndpoint)
                .orElseThrow(() -> new Exception("No endpoint was received from Connect Token response"));
    }

    private String getTokenFromConnectTokenResponse(JsonKucoinResponseDTO response) throws Exception {
        return Optional.ofNullable(response)
                .map(JsonKucoinResponseDTO::getData)
                .map(KucoinDataDTO::getToken)
                .orElseThrow(() -> new Exception("No token was received from Connect Token response"));
    }

    private Integer getPingIntervalFromConnectTokenResponse(JsonKucoinResponseDTO tokenResponse) throws Exception {
        return Optional.ofNullable(tokenResponse)
                .map(JsonKucoinResponseDTO::getData)
                .map(KucoinDataDTO::getInstanceServers)
                .flatMap(list -> list.stream().findFirst())
                .map(KucoinInstanceServerDTO::getPingInterval)
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


