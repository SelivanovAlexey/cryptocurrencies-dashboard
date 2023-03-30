package com.onedigit.utah.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PropertiesProvider {
    @Value("#{'${api.includeTickers}'.split(',')}")
    @Getter
    private List<String> includeTickers;

}
