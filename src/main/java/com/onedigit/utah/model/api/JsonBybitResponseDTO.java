package com.onedigit.utah.model.api;

import lombok.Value;

@Value
public class JsonBybitResponseDTO {
    BybitResultDTO result;
    Integer retCode;
    String retMsg;
    Object retExtInfo;
    Long time;
}
