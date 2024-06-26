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

    private final Map<TbPeriodType, List<TrendBar>> storage;

    public TrendBarRepositoryStubImpl() {
        storage = new ConcurrentHashMap<>();
        for (TbPeriodType type : TbPeriodType.values()) {
            storage.put(type, new ArrayList<>());
        }
    }

    @Override
    public void saveTrendBar(TrendBar trendBar) {
        storage.get(trendBar.periodType()).add(trendBar);
    }

    @Override
    public void saveTrendBarList(List<TrendBar> trendBarList) {
        trendBarList.forEach(trendBar -> storage.get(trendBar.periodType()).add(trendBar));
    }

    @Override
    public Optional<TbHistory> getTbHistoryByPeriod(String symbol, TbPeriodType type, Instant from, Instant to) {
        Optional<TbHistory> ret = Optional.empty();

        final Instant toTmstmp = (to == null) ? Instant.now() : to;
        Set<TrendBar> filteredTrendBars = storage.get(type)
                .stream()
                .filter(tBar -> from.isBefore(tBar.startTimestamp()) && toTmstmp.isAfter(tBar.endTimestamp()))
                .collect(Collectors.toSet());

        if (!filteredTrendBars.isEmpty()) {
            ret = Optional.of(new TbHistory(filteredTrendBars));
        }

        return ret;
    }

    public Map<TbPeriodType, List<TrendBar>> getStorage() {

        return storage;
    }

}
