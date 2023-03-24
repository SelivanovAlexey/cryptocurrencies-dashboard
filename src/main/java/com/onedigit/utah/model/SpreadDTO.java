package com.onedigit.utah.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpreadDTO {
    private Exchange base;
    private Exchange target;
    private Double diff;
}
