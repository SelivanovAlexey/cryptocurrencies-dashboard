package com.onedigit.utah.model.event;

import com.onedigit.utah.model.CoinDTO;

@FunctionalInterface
public interface PriceChangeEventListener {
    void onPublish(CoinDTO coinUpdateDTO);
}
