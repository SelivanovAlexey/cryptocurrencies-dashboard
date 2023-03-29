package com.onedigit.utah.model.event;

@FunctionalInterface
public interface PriceChangeEventListener {
    void onPublish(CoinUpdateDTO coinUpdateDTO);
}
