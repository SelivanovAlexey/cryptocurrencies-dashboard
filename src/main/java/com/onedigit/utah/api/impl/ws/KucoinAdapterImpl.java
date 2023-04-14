package com.onedigit.utah.api.impl.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Connection;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.api.kucoin.rest.KucoinRestData;
import com.onedigit.utah.model.api.kucoin.rest.KucoinRestInstanceServer;
import com.onedigit.utah.model.api.kucoin.rest.KucoinRestResponse;
import com.onedigit.utah.model.api.kucoin.ws.KucoinWsMessage;
import com.onedigit.utah.service.MarketLocalCache;
import com.onedigit.utah.util.CommonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static com.onedigit.utah.constants.ApiConstants.*;

/**
 * Implemented with WebSocket protocol
 */
@Slf4j
@Service
public class KucoinAdapterImpl extends BaseExchangeAdapter {
    private final WebSocketClient webSocketClient;
    private final ObjectMapper mapper;
    private WebSocketSession session;
    private String subscribeMessage;
    private String pingMessage;


    public KucoinAdapterImpl(@Qualifier("webSocketClient") WebSocketClient webSocketClient,
                             @Qualifier("kucoinRestApiClient") WebClient kucoinRestApiClient, ObjectMapper mapper) {
        this.webSocketClient = webSocketClient;
        this.webClient = kucoinRestApiClient;
        this.mapper = mapper;
    }

    @Override
    @SneakyThrows
    public void getMarketData() {
        log.info("Started getMarkedData from kucoin");
        KucoinRestResponse tokenResponse = getConnectToken();
        String endpoint = getEndpointFromConnectTokenResponse(tokenResponse);
        String token = getTokenFromConnectTokenResponse(tokenResponse);
        Integer pingInterval = getPingIntervalFromConnectTokenResponse(tokenResponse);
        log.debug("getConnectToken info: {}", tokenResponse);

        //TODO: to refactor with handle method and generalize
        webSocketClient.execute(
                URI.create(endpoint + "?token=" + token + "&connectId=" + UUID.randomUUID()),
                session -> {
                    this.session = session;
                    Mono<Void> mainFlow = session.receive()
                            .flatMap(msg -> {
                                //TODO: to replace with general error handling
                                try {
                                    String payload = msg.getPayloadAsText();
                                    KucoinWsMessage message = mapper.readValue(payload, KucoinWsMessage.class);
                                    return handleKucoinResponse(message, session);
                                } catch (Exception e) {
                                    log.error("unexpected exception in kucoin flux", e);
                                    return Flux.error(e);
                                }
                            })
                            .then();

                    Mono<Void> pingFlow = session.send(Flux.interval(Duration.ofMillis(pingInterval)).flatMap(interval -> {
                        log.debug("Send ping: {}", pingMessage);
                        return Mono.just(session.textMessage(pingMessage));
                    }));
                    return Flux.merge(mainFlow, pingFlow).then();
                }).retryWhen(exchangeApiRetrySpec(log)).subscribe();
    }

    private Mono<Void> handleKucoinResponse(KucoinWsMessage response, WebSocketSession session) {
        Mono<WebSocketMessage> sessionResponse = Mono.empty();
        switch (MessageTypes.valueOf(response.getType().toUpperCase())) {
            case WELCOME -> {
                log.debug("Received welcome message: {}", response.asJsonString());
                subscribeMessage = CommonUtils.buildKucoinSubscribeMessage(response.getId(),
                        KUCOIN_TOPIC_MARKET_DATA);
                pingMessage = CommonUtils.buildKucoinPingMessage(response.getId());
                sessionResponse = Mono.just(session.textMessage(subscribeMessage));
            }
            case MESSAGE -> {
                if (response.getSubject().endsWith("USDT")) {
                    String ticker = StringUtils.substringBefore(response.getSubject(), "-USDT");
                    BigDecimal price = new BigDecimal(response.getData().getPrice());
                    MarketLocalCache.put(ticker, getExchangeName(), price);
                }
            }
            case PONG -> log.debug("Received pong: {}", response.asJsonString());
        }
        return session.send(sessionResponse);
    }

    private KucoinRestResponse getConnectToken() {
        return post(KUCOIN_API_REST_GET_CONNECT_TOKEN_URL, KucoinRestResponse.class).block();
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

    public Boolean isEnabled() {
        return true;
    }

    @Override
    public Connection getConnectionStatus() {
        return session != null && session.isOpen() ? Connection.ACTIVE : Connection.INACTIVE;

    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.KUCOIN;
    }

    enum MessageTypes {
        WELCOME, MESSAGE, PONG
    }

}


