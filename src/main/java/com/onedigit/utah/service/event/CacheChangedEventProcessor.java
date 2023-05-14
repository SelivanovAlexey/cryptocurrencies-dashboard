package com.onedigit.utah.service.event;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.event.CacheChangedEventListener;
import lombok.Getter;
import org.springframework.stereotype.Service;

//TODO: generalize
//TODO: to make support of multiple listeners
@Service
public class CacheChangedEventProcessor {
    @Getter
    private static CacheChangedEventListener listener;

    public void register(CacheChangedEventListener listener) {
        CacheChangedEventProcessor.listener = listener;
    }

    public static void publish(CoinDTO coinUpdateDTO) {
        if (listener != null)
            CacheChangedEventProcessor.listener.onChanged(coinUpdateDTO);
    }
}
