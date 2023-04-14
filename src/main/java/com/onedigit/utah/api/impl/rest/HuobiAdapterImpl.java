package com.onedigit.utah.api.impl.rest;

import com.onedigit.utah.api.impl.BaseExchangeAdapter;
import com.onedigit.utah.model.Exchange;

/**
 * Implemented with REST protocol
 */
public class HuobiAdapterImpl extends BaseExchangeAdapter {
    @Override
    public void getMarketData() {
    }

    @Override
    public Boolean isEnabled() {
        return false;
    }

    @Override
    public Exchange getExchangeName() {
        return Exchange.HUOBI;
    }
}
