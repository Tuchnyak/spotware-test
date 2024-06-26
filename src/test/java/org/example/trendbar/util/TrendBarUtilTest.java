package org.example.trendbar.util;

import org.example.trendbar.model.Quote;
import org.example.trendbar.model.TbPeriodType;
import org.example.trendbar.model.TrendBar;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static org.junit.Assert.*;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class TrendBarUtilTest {

    private final TimestampUtil timestampUtil = new TimestampUtil();
    private TrendBarUtil underTest = new TrendBarUtil(timestampUtil);

    @Test
    public void getNewTrendBarFromQuote() {
        Instant instant = Instant.now();
        Quote quote = new Quote(BigDecimal.TEN, "USDAMD", instant);

        TrendBar actual = underTest.getNewTrendBarFromQuote(quote, TbPeriodType.D1);

        assertNotNull(actual);
        assertEquals("USDAMD", actual.symbol());
        assertEquals(BigDecimal.TEN, actual.openPrice());
        assertEquals(BigDecimal.TEN, actual.closePrice());
        assertEquals(BigDecimal.TEN, actual.lowPrice());
        assertEquals(BigDecimal.TEN, actual.highPrice());
        assertEquals(TbPeriodType.D1, actual.periodType());
        assertEquals(timestampUtil.roundByPeriod(instant, TbPeriodType.D1).toString(), actual.startTimestamp().toString());
        assertEquals(timestampUtil.roundByPeriod(instant.plus(1, ChronoUnit.DAYS), TbPeriodType.D1).toString(), actual.endTimestamp().toString());
    }

    @Test
    public void copyWithUpdateLogic() {
        Instant instant_1 = Instant.now();
        Instant instant_2 = instant_1.plus(1, ChronoUnit.HOURS);
        Quote quote_1 = new Quote(BigDecimal.TEN, "USDKZT", instant_1);
        Quote quote_2 = new Quote(BigDecimal.ONE, "USDKZT", instant_2);
        TrendBar currentTb = underTest.getNewTrendBarFromQuote(quote_1, TbPeriodType.D1);

        TrendBar actual = underTest.copyWithUpdateLogic(currentTb, quote_2);

        assertNotNull(actual);
        assertEquals("USDKZT", actual.symbol());
        assertEquals(BigDecimal.TEN, actual.openPrice());
        assertEquals(BigDecimal.ONE, actual.closePrice());
        assertEquals(BigDecimal.ONE, actual.lowPrice());
        assertEquals(BigDecimal.TEN, actual.highPrice());
        assertEquals(TbPeriodType.D1, actual.periodType());
        assertEquals(currentTb.startTimestamp(), actual.startTimestamp());
        assertEquals(currentTb.endTimestamp(), actual.endTimestamp());
    }

}