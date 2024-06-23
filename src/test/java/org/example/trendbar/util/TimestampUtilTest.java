package org.example.trendbar.util;

import org.example.trendbar.model.TbPeriodType;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class TimestampUtilTest {

    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final Instant INSTANT_TO_PROCESS = LocalDateTime.of(2000, 1, 1, 12, 33, 42)
            .atZone(ZONE_ID)
            .toInstant();

    private final TimestampUtil undertest = new TimestampUtil();

    @Test
    public void roundByPeriod_M1() {
        TbPeriodType type = TbPeriodType.M1;
        Instant actual = undertest.roundByPeriod(INSTANT_TO_PROCESS, type);

        assertEquals("2000-01-01T12:33:00Z", actual.toString());
    }

    @Test
    public void roundByPeriod_H1() {
        TbPeriodType type = TbPeriodType.H1;
        Instant actual = undertest.roundByPeriod(INSTANT_TO_PROCESS, type);

        assertEquals("2000-01-01T12:00:00Z", actual.toString());
    }

    @Test
    public void roundByPeriod_D1() {
        TbPeriodType type = TbPeriodType.D1;
        Instant actual = undertest.roundByPeriod(INSTANT_TO_PROCESS, type);

        assertEquals("2000-01-01T00:00:00Z", actual.toString());
    }

}