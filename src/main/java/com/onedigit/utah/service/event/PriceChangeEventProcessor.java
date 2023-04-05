package com.onedigit.utah.service.event;

import com.onedigit.utah.model.CoinDTO;
import com.onedigit.utah.model.event.PriceChangeEventListener;
import lombok.Getter;
import org.springframework.stereotype.Service;

//TODO: generalize
//TODO: to make support of multiple listeners
@Service
public class PriceChangeEventProcessor {
    @Getter
    private static PriceChangeEventListener listener;

    public void register(PriceChangeEventListener listener) {
        PriceChangeEventProcessor.listener = listener;
    }

    public static void publish(CoinDTO coinUpdateDTO) {
        if (listener != null)
            PriceChangeEventProcessor.listener.onPublish(coinUpdateDTO);
    }
}
