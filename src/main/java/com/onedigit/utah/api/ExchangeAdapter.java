package com.onedigit.utah.api;

import com.onedigit.utah.model.Exchange;

public interface ExchangeAdapter {

    void getMarketData();

    Boolean isEnabled();

    Exchange getExchangeName();
}
