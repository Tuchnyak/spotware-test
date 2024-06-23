package org.example.trendbar.service;

import org.example.trendbar.model.Quote;
import org.example.trendbar.model.TbHistory;
import org.example.trendbar.model.TbPeriodType;

import java.time.Instant;

/**
 * @author tuchnyak (George Shchennikov)
 */
public interface TrendBarService {

    void updateTrendBar(Quote quote);
    TbHistory getTrendBarHistoryByPeriod(String symbol, TbPeriodType type, Instant from, Instant to);

}
