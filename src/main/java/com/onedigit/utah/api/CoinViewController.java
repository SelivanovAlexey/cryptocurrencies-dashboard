package com.onedigit.utah.api;

import com.onedigit.utah.model.api.CoinView;
import com.onedigit.utah.service.event.publishers.CoinViewPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RequestMapping("/api")
@RestController
//TODO: remove hardcode
@CrossOrigin(origins = "http://localhost:63342")
public class CoinViewController {

    private final CoinViewPublisher publisher;

    public CoinViewController(CoinViewPublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping(path = "/spreads", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<CoinView>> spreads() {
        return publisher.spreads().map(v -> toSse(v, "spreads"));
    }

    @GetMapping(path = "/info/{ticker}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<CoinView>> detail(@PathVariable String ticker) {
        return publisher.detail(ticker).map(v -> toSse(v, "verbose"));
    }

    private ServerSentEvent<CoinView> toSse(CoinView view, String event) {
        return ServerSentEvent.<CoinView>builder().event(event).data(view).build();
    }
}
