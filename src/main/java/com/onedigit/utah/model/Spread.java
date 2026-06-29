package com.onedigit.utah.model;

public record Spread(Exchange base,
                     Exchange target,
                     double diff) {
}
