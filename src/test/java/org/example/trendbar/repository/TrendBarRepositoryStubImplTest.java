package org.example.trendbar.repository;

import org.example.trendbar.model.TbHistory;
import org.example.trendbar.model.TbPeriodType;
import org.example.trendbar.model.TrendBar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class TrendBarRepositoryStubImplTest {

    private TrendBarRepository underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new TrendBarRepositoryStubImpl();
    }

    @After
    public void tearDown() throws Exception {
        underTest = null;
    }

    @Test
    public void saveTrendBar() {
        TrendBar tb1 = new TrendBar.Builder().periodType(TbPeriodType.D1).startTimestamp(Instant.now()).build();
        TrendBar tb2 = new TrendBar.Builder().periodType(TbPeriodType.D1).startTimestamp(Instant.now()).build();
        TrendBar tb3 = new TrendBar.Builder().periodType(TbPeriodType.M1).startTimestamp(Instant.now()).build();

        underTest.saveTrendBar(tb1);
        underTest.saveTrendBar(tb2);
        underTest.saveTrendBar(tb3);

        var storage = ((TrendBarRepositoryStubImpl) underTest).getStorage();

        assertNotNull(storage);
        assertEquals(TbPeriodType.values().length, storage.size());
        assertEquals(2, storage.get(TbPeriodType.D1).size());
        assertEquals(1, storage.get(TbPeriodType.M1).size());
    }

    @Test
    public void getTbHistoryByPeriod() {
        TrendBar tb1 = new TrendBar.Builder()
                .periodType(TbPeriodType.M1)
                .symbol("USDAMD")
                .startTimestamp(Instant.now().minus(15, ChronoUnit.MINUTES))
                .build();
        TrendBar tb2 = new TrendBar.Builder()
                .periodType(TbPeriodType.M1)
                .symbol("USDAMD")
                .startTimestamp(Instant.now().minus(9, ChronoUnit.MINUTES))
                .build();
        TrendBar tb3 = new TrendBar.Builder()
                .periodType(TbPeriodType.M1)
                .symbol("USDAMD")
                .startTimestamp(Instant.now().minus(8, ChronoUnit.MINUTES))
                .build();
        TrendBar tb4 = new TrendBar.Builder()
                .periodType(TbPeriodType.M1)
                .symbol("USDAMD")
                .startTimestamp(Instant.now().minus(1, ChronoUnit.MINUTES))
                .build();
        TrendBar tb5 = new TrendBar.Builder()
                .periodType(TbPeriodType.D1)
                .symbol("USDAMD")
                .startTimestamp(Instant.now().minus(7, ChronoUnit.MINUTES))
                .build();
        underTest.saveTrendBar(tb1);
        underTest.saveTrendBar(tb2);
        underTest.saveTrendBar(tb3);
        underTest.saveTrendBar(tb4);
        underTest.saveTrendBar(tb5);

        Optional<TbHistory> actual = underTest.getTbHistoryByPeriod(
                "USDAMD",
                TbPeriodType.M1,
                Instant.now().minus(600, ChronoUnit.SECONDS),
                Instant.now().minus(400, ChronoUnit.SECONDS)
        );

        assertTrue(actual.isPresent());
        assertFalse(actual.get().trendBarSet().isEmpty());
        assertEquals(2, actual.get().trendBarSet().size());
    }

    @Test
    public void saveTrendBarList() {
        Map<TbPeriodType, List<TrendBar>> storage = ((TrendBarRepositoryStubImpl) underTest).getStorage();
        List<TrendBar> listToSave = List.of(
                new TrendBar.Builder()
                        .periodType(TbPeriodType.M1)
                        .symbol("USDAMD")
                        .startTimestamp(Instant.now())
                        .build(),
                new TrendBar.Builder()
                        .periodType(TbPeriodType.M1)
                        .symbol("USDAMD")
                        .startTimestamp(Instant.now())
                        .build()
        );
        assertEquals(0, storage.get(TbPeriodType.M1).size());

        underTest.saveTrendBarList(listToSave);

        assertEquals(2, storage.get(TbPeriodType.M1).size());
    }

}