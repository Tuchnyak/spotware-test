package org.example.trendbar.service;

import org.example.trendbar.model.Quote;
import org.example.trendbar.model.TbHistory;
import org.example.trendbar.model.TbPeriodType;
import org.example.trendbar.model.TrendBar;
import org.example.trendbar.repository.TrendBarRepository;
import org.example.trendbar.util.TimestampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tuchnyak (George Shchennikov)
 */
@Service
public class TrendBarServiceImpl implements TrendBarService {

    private final Logger logger = LoggerFactory.getLogger(TrendBarServiceImpl.class);

    private final Map<TbPeriodType, Map<String, TrendBar>> currentTrendBars;
    private final TrendBarRepository trendBarRepository;
    private final TimestampUtil timestampUtil;

    public TrendBarServiceImpl(TrendBarRepository trendBarRepository, TimestampUtil timestampUtil) {
        this.trendBarRepository = trendBarRepository;
        this.timestampUtil = timestampUtil;
        currentTrendBars = initiateCurrentTrendBarMap();
    }

    @Override
    public void updateTrendBar(Quote quote) {
        String symbol = quote.symbol();
        BigDecimal price = quote.newPrice();
        Instant timestamp = quote.timestamp();

        Arrays.stream(TbPeriodType.values())
                .forEach(periodType -> {
                    Map<String, TrendBar> typeSymbolTrendBarMap = currentTrendBars.get(periodType);
                    boolean alreadyContained = typeSymbolTrendBarMap.containsKey(symbol);
                    TrendBar currentTb = typeSymbolTrendBarMap.computeIfAbsent(
                            symbol,
                            k -> new TrendBar.Builder()
                                .symbol(symbol)
                                .openPrice(price).closePrice(price).highPrice(price).lowPrice(price)
                                .periodType(periodType)
                                .startTimestamp(timestampUtil.roundByPeriod(timestamp, periodType))
                                .build()
                    );
                    if (alreadyContained) {
                        if (timestamp.isBefore(currentTb.getEndTimestamp())) {
                            // update current
                        } else {
                            // save current
                            // put new
                        }
                    }
                });

    }

    @Override
    public TbHistory getTrendBarHistoryByPeriod(String symbol, TbPeriodType type, Instant from, Instant to) {
        return null;
    }

    private Map<TbPeriodType, Map<String, TrendBar>> initiateCurrentTrendBarMap() {
        Map<TbPeriodType, Map<String, TrendBar>> retMap = new ConcurrentHashMap<>();
        Arrays.stream(TbPeriodType.values())
                .forEach(type -> {
                    retMap.put(type, new ConcurrentHashMap<>());
                });

        return retMap;
    }

}
