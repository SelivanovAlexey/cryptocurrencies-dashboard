package com.onedigit.utah.model.api.bybit.ws;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@Jacksonized
public class BybitWsResponse extends JsonSerializable {
    String topic;
    Long ts;
    String type;
    Long cs;
    BybitWsData data;
}
