//package com.onedigit.utah.integration.impl.rest;
//
//import com.onedigit.utah.integration.impl.BaseExchangeAdapter;
//import com.onedigit.utah.model.Exchange;
//import com.onedigit.utah.model.events.PriceUpdate;
//import com.onedigit.utah.model.integration.common.RestResponse;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//
//public class HuobiAdapterImpl extends BaseExchangeAdapter {
//    @Override
//    public Flux<? extends RestResponse> watchPrices() {
//        return null;
//    }
//
//    @Override
//    public List<PriceUpdate> toPriceUpdate(RestResponse response) {
//        return List.of();
//    }
//
//    @Override
//    public Exchange getExchangeName() {
//        return Exchange.HUOBI;
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
