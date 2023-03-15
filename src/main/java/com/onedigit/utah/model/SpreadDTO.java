package com.onedigit.utah.model;

import lombok.Data;

@Data
public class SpreadDTO {
    private Exchange base;
    private Exchange target;
    private Double diff;
}
