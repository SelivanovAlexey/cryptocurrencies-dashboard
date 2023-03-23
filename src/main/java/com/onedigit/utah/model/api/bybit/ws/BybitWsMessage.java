package com.onedigit.utah.model.api.bybit.ws;

import com.onedigit.utah.util.JsonSerializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@Jacksonized
public class BybitWsMessage extends JsonSerializable {
    String req_id;
    String op;
    List<String> args;
    String ret_msg;
    Boolean success;
}
