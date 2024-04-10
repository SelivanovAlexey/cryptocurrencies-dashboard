package com.onedigit.utah.model2.integration.bybit.ws;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BybitWsResponse {
    String topic;
    Long ts;
    String type;
    Long cs;
    BybitWsData data;
}
