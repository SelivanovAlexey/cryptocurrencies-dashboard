package com.onedigit.utah.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedigit.utah.model.api.kucoin.JsonKucoinResponseDTO;
import com.onedigit.utah.model.api.kucoin.KucoinDataDTO;
import com.onedigit.utah.model.api.kucoin.KucoinInstanceServerDTO;
import com.onedigit.utah.model.api.kucoin.messages.KucoinWsMessage;
import com.onedigit.utah.rest.api.ExchangeAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.DataInput;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;

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

        //TODO: make ping-pong logic
        //TODO: make storing data in non-blocking cache map
        return kucoinWsApiClient.execute(
                URI.create(endpoint + "?token=" + token),
                session ->
                        session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .log()
                                .map(payload -> {
                                    //TODO: replace with general error handling
                                    try {
                                        return mapper.readValue(payload, KucoinWsMessage.class);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .flatMap(message -> {
                                    if (message.getType().equals("welcome")){
                                        return session.send(Mono.just(session.textMessage(buildSubscribeMessage(KUCOIN_TOPIC_MARKET_DATA)))).log();
                                    }
                                    if (message.getType().equals("message")){
                                       return session.send(Mono.empty());
                                    }
                                    return session.send(Mono.empty());
                                })
                                .then());
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

    private String buildSubscribeMessage(String topic) {
        return
                KucoinWsMessage.builder()
                        .topic(topic)
                        .type("subscribe")
                        .build().asJsonString(mapper);
    }
}


