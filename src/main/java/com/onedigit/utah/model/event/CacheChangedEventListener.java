package com.onedigit.utah.model.event;

import com.onedigit.utah.model.CoinDTO;

@FunctionalInterface
public interface CacheChangedEventListener {
    void onChanged(CoinDTO coinUpdateDTO);
}
