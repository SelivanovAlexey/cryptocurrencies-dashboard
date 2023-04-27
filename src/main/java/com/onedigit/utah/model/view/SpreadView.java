package com.onedigit.utah.model.view;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.SpreadDTO;
import lombok.NonNull;

import java.util.List;

public class SpreadView extends CoinDTO {
    public SpreadView(@NonNull String ticker, List<SpreadDTO> spreads) {
        super(ticker, spreads);
    }
}