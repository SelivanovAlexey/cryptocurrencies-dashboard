//package com.onedigit.utah.api.impl.ws;
//
//import com.onedigit.utah.api.ExchangeAdapter;
//import com.onedigit.utah.model.Exchange;
//import com.onedigit.utah.model.api.common.RestResponse;
//import reactor.core.publisher.Flux;
//
//
///**
// * Implemented with WebSocket protocol
// */
//public class BinanceAdapterImpl implements ExchangeAdapter {
//    @Override
//    public Flux<RestResponse> watchPrices() {
//        return null;
//    }
//
//    @Override
//    public void storePrice(RestResponse response) {
//
//    }
//
//
//
//    @Override
//    public Exchange getExchangeName() {
//        return Exchange.BINANCE;
//    }
//
//    @Override
//    public Flux<? extends RestResponse> watchAvailability() {
//        return null;
//    }
//
//    @Override
//    public void storeAvailability(RestResponse response) {
//
//    }
//}
