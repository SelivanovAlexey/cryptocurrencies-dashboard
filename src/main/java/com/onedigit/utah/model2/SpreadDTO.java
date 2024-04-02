package com.onedigit.utah.model2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpreadDTO {
    private String ticker;
    private Exchange base;
    private Exchange target;
    private Double diff;
}
