package com.onedigit.utah.model.api.kucoin.ws;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@Jacksonized
public class KucoinWsMessage extends JsonSerializable {
    String id;
    String type;
    String topic;
    String subject;
    KucoinWsData data;
    Boolean privateChannel = false;
    Boolean response = false;


}
