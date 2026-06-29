package com.onedigit.utah.service.event.publishers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "coin.cache", name = "mode", havingValue = "live", matchIfMissing = true)
public class LivePublisher extends CoinViewPublisher{ }
