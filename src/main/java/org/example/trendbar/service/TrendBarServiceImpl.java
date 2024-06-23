package org.example.trendbar.service;

import org.example.trendbar.model.Quote;
import org.example.trendbar.model.TbHistory;
import org.example.trendbar.model.TbPeriodType;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author tuchnyak (George Shchennikov)
 */
@Service
public class TrendBarServiceImpl implements TrendBarService {
    @Override
    public void updateTrendBar(Quote quote) {

    }

    @Override
    public TbHistory getTrendBarHistoryByPeriod(String symbol, TbPeriodType type, Instant from, Instant to) {
        return null;
    }
}
