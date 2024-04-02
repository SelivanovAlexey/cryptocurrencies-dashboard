package com.onedigit.utah.model2.event;

import com.onedigit.utah.model2.CoinDTO;

@FunctionalInterface
public interface CacheChangedEventListener {
    void onChanged(CoinDTO coinUpdateDTO);
}
