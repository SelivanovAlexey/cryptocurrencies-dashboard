package com.onedigit.utah.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedigit.utah.model.api.kucoin.ws.KucoinWsMessage;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {

    static ObjectMapper mapper;

    public CommonUtils(ObjectMapper mapper) {
        CommonUtils.mapper = mapper;
    }

    public static ObjectMapper getJsonMapper() {
        return mapper;
    }

    public static String buildKucoinPingMessage(String id) {
        return KucoinWsMessage.builder()
                .id(id)
                .type("ping")
                .build().asJsonString();
    }

    public static String buildKucoinSubscribeMessage(String id, String topic) {
        return KucoinWsMessage.builder()
                .id(id)
                .topic(topic)
                .type("subscribe")
                .build().asJsonString();
    }


}
