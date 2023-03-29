package com.onedigit.utah.service.event;

import com.onedigit.utah.model.event.CoinUpdateDTO;
import com.onedigit.utah.model.event.PriceChangeEventListener;
import org.springframework.stereotype.Service;

//TODO: generalize
//TODO: to make support of multiple listeners
@Service
public class PriceChangeEventProcessor {
    private static PriceChangeEventListener listener;

    public void register(PriceChangeEventListener listener) {
        PriceChangeEventProcessor.listener = listener;
    }

    public static void publish(CoinUpdateDTO coinUpdateDTO) {
        if (listener != null)
            PriceChangeEventProcessor.listener.onPublish(coinUpdateDTO);
    }
}
