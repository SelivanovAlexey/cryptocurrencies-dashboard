package com.onedigit.utah.model.integration.kucoin.ws;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class KucoinWsMessage {
    String id;
    String type;
    String topic;
    String subject;
    KucoinWsData data;
    Boolean privateChannel = false;
    Boolean response = false;


}
