package org.example.trendbar.repository;

import org.example.trendbar.model.TbHistory;
import org.example.trendbar.model.TbPeriodType;
import org.example.trendbar.model.TrendBar;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author tuchnyak (George Shchennikov)
 */
public interface TrendBarRepository {

    void saveTrendBar(TrendBar trendBar);
    void saveTrendBarList(List<TrendBar> trendBarList);
    Optional<TbHistory> getTbHistoryByPeriod(String symbol, TbPeriodType type, Instant from, Instant to);

}
