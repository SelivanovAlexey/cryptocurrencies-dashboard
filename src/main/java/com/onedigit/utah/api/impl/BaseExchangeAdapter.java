package com.onedigit.utah.api.impl;

import com.onedigit.utah.api.ExchangeAdapter;
import com.onedigit.utah.api.common.RestApiAdapter;
import com.onedigit.utah.model.Connection;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseExchangeAdapter extends RestApiAdapter implements ExchangeAdapter {
    @Getter
    @Setter
    protected Connection connectionStatus = Connection.INACTIVE;
}
