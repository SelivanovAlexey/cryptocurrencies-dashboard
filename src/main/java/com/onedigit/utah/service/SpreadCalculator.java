package com.onedigit.utah.service;

import com.onedigit.utah.model.Exchange;
import com.onedigit.utah.model.Spread;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SpreadCalculator {
    //TODO: hardcoded limit for spreads?
    private static final int MAX_SPREADS = 3;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /**
     * Each vs each + sorting implementation (O(n^2 log n))
     */
    @Deprecated
    public List<Spread> calculateSpreadsOld(Map<Exchange, BigDecimal> prices) {
        List<Spread> spreads = new ArrayList<>();
        for (Map.Entry<Exchange, BigDecimal> base : prices.entrySet()) {
            BigDecimal basePrice = base.getValue();
            if (basePrice.compareTo(BigDecimal.ZERO) == 0) continue;
            for (Map.Entry<Exchange, BigDecimal> target : prices.entrySet()) {
                if (base.getKey().equals(target.getKey())) continue;
                BigDecimal targetPrice = target.getValue();
                if (targetPrice.compareTo(basePrice) <= 0) continue;
                double diff = targetPrice.subtract(basePrice)
                        .multiply(HUNDRED)
                        .divide(basePrice, 2, RoundingMode.HALF_UP)
                        .doubleValue();
                spreads.add(new Spread(base.getKey(), target.getKey(), diff));
            }
        }
        spreads.sort(Comparator.comparingDouble(Spread::diff).reversed());
        return spreads.stream().limit(MAX_SPREADS).toList();
    }

    /**
     * Priority queue implementation (O(n^2 log k))
     */
    public List<Spread> calculateSpreads(Map<Exchange, BigDecimal> prices) {
        PriorityQueue<Spread> top = new PriorityQueue<>(
                Comparator.comparingDouble(Spread::diff)); // min-heap

        for (var base : prices.entrySet()) {
            BigDecimal basePrice = base.getValue();
            if (basePrice.signum() == 0) continue;
            for (var target : prices.entrySet()) {
                if (base.getKey().equals(target.getKey())) continue;
                BigDecimal targetPrice = target.getValue();
                if (targetPrice.compareTo(basePrice) <= 0) continue;

                double diff = targetPrice.subtract(basePrice)
                        .multiply(HUNDRED)
                        .divide(basePrice, 2, RoundingMode.HALF_UP)
                        .doubleValue();

                if (top.size() < MAX_SPREADS) {
                    top.offer(new Spread(base.getKey(), target.getKey(), diff));
                } else if (diff > top.peek().diff()) {
                    top.poll();
                    top.offer(new Spread(base.getKey(), target.getKey(), diff));
                }
            }
        }

        return top.stream()
                .sorted(Comparator.comparingDouble(Spread::diff).reversed())
                .toList();

    }
}