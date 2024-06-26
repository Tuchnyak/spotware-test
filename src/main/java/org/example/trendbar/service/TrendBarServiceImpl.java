package org.example.trendbar.service;

import org.example.trendbar.model.Quote;
import org.example.trendbar.model.TbHistory;
import org.example.trendbar.model.TbPeriodType;
import org.example.trendbar.model.TrendBar;
import org.example.trendbar.repository.TrendBarRepository;
import org.example.trendbar.util.TrendBarUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tuchnyak (George Shchennikov)
 */
@Service
public class TrendBarServiceImpl implements TrendBarService, ApplicationListener<ContextClosedEvent> {

    private final Logger logger = LoggerFactory.getLogger(TrendBarServiceImpl.class);

    private final Map<String, Map<TbPeriodType, TrendBar>> currentTrendBars;
    private final Map<String, Lock> symbolLockMap;

    private final TrendBarRepository trendBarRepository;
    private final TrendBarUtil tbUtil;

    public TrendBarServiceImpl(TrendBarRepository trendBarRepository, TrendBarUtil tbUtil) {
        this.trendBarRepository = trendBarRepository;
        this.tbUtil = tbUtil;
        currentTrendBars = new ConcurrentHashMap<>();
        symbolLockMap = new ConcurrentHashMap<>();
    }

    @Override
    public void updateTrendBar(Quote quote) {
        logger.debug("Processing quote: {}", quote);
        String qtSymbol = quote.symbol();

        Lock lock = getLockBySymbol(qtSymbol);
        lock.lock();
        try {
            boolean containedBefore = currentTrendBars.containsKey(qtSymbol);
            Map<TbPeriodType, TrendBar> symbolCurrentTrendBarsMap = getSymbolBranch(quote, currentTrendBars);
            if (containedBefore) {
                Map<TbPeriodType, TrendBar> updatedSymbolMap = new ConcurrentHashMap<>();
                symbolCurrentTrendBarsMap.forEach((periodType, curTrendBar) -> {
                    if (quote.timestamp().isAfter(curTrendBar.endTimestamp())) {
                        trendBarRepository.saveTrendBar(curTrendBar);
                        TrendBar newTrendBar = tbUtil.getNewTrendBarFromQuote(quote, periodType);
                        updatedSymbolMap.put(periodType, newTrendBar);
                        logger.debug("Saved current trend bar: {} and put the new one: {}", curTrendBar, newTrendBar);
                    } else {
                        TrendBar updatedTbCopy = tbUtil.copyWithUpdateLogic(curTrendBar, quote);
                        updatedSymbolMap.put(periodType, updatedTbCopy);
                        logger.debug("Trend bar {} has been updated: {}", curTrendBar, updatedTbCopy);
                    }
                });
                currentTrendBars.put(qtSymbol, updatedSymbolMap);
            } else {
                logger.debug("Created new trend bar branch for {}", quote);
            }
        } finally {
            lock.unlock();
        }

        logger.debug("Quote has been processed: {}", quote);
    }

    @Override
    public TbHistory getTrendBarHistoryByPeriod(String symbol, TbPeriodType type, Instant from, Instant to) {

        return trendBarRepository
                .getTbHistoryByPeriod(symbol, type, from, to)
                .orElse(new TbHistory(Collections.emptySet()));
    }

    private Lock getLockBySymbol(String quoteSymbol) {

        return symbolLockMap.computeIfAbsent(quoteSymbol, k -> new ReentrantLock(true));
    }

    private Map<TbPeriodType, TrendBar> getSymbolBranch(Quote quote, Map<String, Map<TbPeriodType, TrendBar>> currentTrendBars) {

        return currentTrendBars.computeIfAbsent(
                quote.symbol(),
                k -> {
                    Map<TbPeriodType, TrendBar> symbolMapBranch = new ConcurrentHashMap<>(TbPeriodType.values().length);
                    Arrays.stream(TbPeriodType.values()).forEach(
                            periodType -> {
                                symbolMapBranch.put(
                                        periodType,
                                        tbUtil.getNewTrendBarFromQuote(quote, periodType)
                                );
                            }
                    );

                    return symbolMapBranch;
                }
        );
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info(">>> Save current trend bars before context close");
        List<TrendBar> trendBarList = currentTrendBars.entrySet().stream()
                .flatMap(symbolMapEntry -> symbolMapEntry.getValue().entrySet().stream())
                .map(Map.Entry::getValue)
                .toList();
        trendBarRepository.saveTrendBarList(trendBarList);
        logger.info(">>> Current trend bars have been before context close");
    }

    public Map<String, Map<TbPeriodType, TrendBar>> getCurrentTrendBars() {

        return currentTrendBars;
    }

    public Map<String, Lock> getSymbolLockMap() {

        return symbolLockMap;
    }

}
