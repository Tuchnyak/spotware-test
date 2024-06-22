package org.example.trendbar.repository;

import org.example.trendbar.model.TbHistory;
import org.example.trendbar.model.TbPeriodType;
import org.example.trendbar.model.TrendBar;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author tuchnyak (George Shchennikov)
 */
@Repository
public class TrendBarRepositoryStubImpl implements TrendBarRepository {

    private static final Map<TbPeriodType, List<TrendBar>> STORAGE = new ConcurrentHashMap<>();

    static {
        for (TbPeriodType type : TbPeriodType.values()) {
            STORAGE.put(type, new ArrayList<>());
        }
    }

    @Override
    public void saveTrendBar(TrendBar trendBar) {
        STORAGE.get(trendBar.getPeriodType()).add(trendBar);
    }

    @Override
    public Optional<TbHistory> getTbHistoryByPeriod(String symbol, TbPeriodType type, Instant from, Instant to) {
        Optional<TbHistory> ret = Optional.empty();

        final Instant toTmstmp = (to == null) ? Instant.now() : to;
        Set<TrendBar> filteredTrendBars = STORAGE.get(type)
                .stream()
                .filter(tBar -> from.isBefore(tBar.getStartTimestamp()) && toTmstmp.isAfter(tBar.getEndTimestamp()))
                .collect(Collectors.toSet());

        if (!filteredTrendBars.isEmpty()) {
            ret = Optional.of(new TbHistory(filteredTrendBars));
        }

        return ret;
    }

    Map<TbPeriodType, List<TrendBar>> getStorage() {

        return STORAGE;
    }

}
