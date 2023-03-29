package com.onedigit.utah.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {

    static ObjectMapper mapper;

    public CommonUtils(ObjectMapper mapper) {
        CommonUtils.mapper = mapper;
    }

    public static ObjectMapper getJsonMapper(){
        return mapper;
    }
}
