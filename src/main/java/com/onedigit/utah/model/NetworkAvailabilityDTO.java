package com.onedigit.utah.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NetworkAvailabilityDTO {
    String networkChain;
    boolean isAvailable;
}
