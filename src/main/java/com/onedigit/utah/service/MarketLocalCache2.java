package com.onedigit.utah.service;

import com.onedigit.utah.model2.CoinDTO;
import com.onedigit.utah.model2.Exchange;
import com.onedigit.utah.model2.NetworkAvailabilityDTO;
import com.onedigit.utah.model2.SpreadDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

//TODO: check locking work correctness
@Service
@Slf4j
public class MarketLocalCache2 {

    private static final List<CoinDTO> coinPricesList = new ArrayList<>();

    private static final List<NetworkAvailabilityDTO> coinChainsAvailabilityList = new ArrayList<>();

    private static List<String> includeTickers;

    private static String lockedTicker = "";
    //TODO: WA - to fix
    @Value("#{'${api.includeTickers}'.split(',')}")
    public void setIncludeTickers(List<String> includeTickers) {
        MarketLocalCache2.includeTickers = includeTickers;
    }

    public CoinDTO savePrice(String ticker, Exchange exchange, BigDecimal price) {
        // initialize if absent
        CoinDTO coinDTO;
        if ((coinDTO = IterableUtils.find(coinPricesList, val -> val.getExchange().equals(exchange) && val.getTicker().equals(ticker))) == null) {
            if (putCondition(ticker)) {
                log.debug("saving {}.{}: {}", exchange, ticker, price);
                coinDTO = CoinDTO.builder().ticker(ticker).price(price).exchange(exchange).build();
                coinPricesList.add(coinDTO);
                return coinDTO;
            }
        } else if (!price.equals(coinDTO.getPrice())
                && !isLockedFor(ticker)
        ) {
            log.debug("updating {}.{}: {}", exchange, ticker, price);
            coinDTO.setPrice(price);
        }
        return coinDTO;
    }

    public List<SpreadDTO> calculateSpreads(CoinDTO coinDTO) {
        // setting lock for current ticker to never be updated during spread calculation
        lockedTicker = coinDTO.getTicker();
        List<SpreadDTO> spreads = new ArrayList<>();
        List<CoinDTO> exchangeCoins = coinPricesList.stream().filter(coin -> coinDTO.getTicker().equals(coin.getTicker()))
                .toList();
        exchangeCoins.forEach(cCoinDTO -> {
            if (!cCoinDTO.getExchange().equals(coinDTO.getExchange())) {
                //TODO: NPE check!
                Double diff =
                        cCoinDTO.getPrice()
                                .subtract(coinDTO.getPrice())
                                .divide(coinDTO.getPrice(), 3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
                SpreadDTO spread = new SpreadDTO(cCoinDTO.getTicker(), coinDTO.getExchange(), cCoinDTO.getExchange(), diff);
                spreads.add(spread);
            }
        });
        spreads.sort(Comparator.comparing(SpreadDTO::getDiff).reversed());
        // releasing ticker lock
        lockedTicker = "";
        return spreads.stream().limit(3).collect(Collectors.toList());
    }


    private boolean putCondition(String ticker) {
        boolean condition = true;
        condition &= !(ticker.endsWith("2S") || ticker.endsWith("3S") || ticker.endsWith("5S") || ticker.endsWith("10S"));
        condition &= !(ticker.endsWith("2L") || ticker.endsWith("3L") || ticker.endsWith("5L") || ticker.endsWith("10L"));
        condition &= includeTickers.get(0).isEmpty() ? true : includeTickers.contains(ticker);
        return condition;
    }

    public boolean isLockedFor(String ticker) {
        return ticker.equals(lockedTicker);
    }

    public boolean hasPricesFor(String ticker) {
        CoinDTO coinDTO = IterableUtils.find(coinPricesList, coin -> ticker.equals(coin.getTicker()));
        return Objects.nonNull(coinDTO);
    }
}
