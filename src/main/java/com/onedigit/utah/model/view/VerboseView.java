package com.onedigit.utah.model.view;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.NetworkAvailabilityDTO;
import com.onedigit.utah.model.SpreadDTO;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class VerboseView extends CoinDTO {
    public VerboseView(@NonNull String ticker, Map<Exchange, BigDecimal> pricesToExchange,
                       List<SpreadDTO> spreads, Map<Exchange, List<NetworkAvailabilityDTO>> networkAvailability) {
        super(ticker, pricesToExchange, spreads, networkAvailability);
    }
}