package org.example.trendbar.service;

import org.example.trendbar.model.Quote;
import org.example.trendbar.model.TbHistory;
import org.example.trendbar.model.TbPeriodType;
import org.example.trendbar.model.TrendBar;
import org.example.trendbar.repository.TrendBarRepository;
import org.example.trendbar.util.TimestampUtil;
import org.example.trendbar.util.TrendBarUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import static org.junit.Assert.*;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class TrendBarServiceTest {

    private TrendBarService underTest;
    private Map<String, Map<TbPeriodType, TrendBar>> currentTbMap;
    private Map<String, Lock> lockMap;

    private TrendBarRepository mockedRepository;
    private TrendBarUtil tbUtilSpy;

    @Before
    public void init() {
        TrendBarUtil tbUtilTmpRef = new TrendBarUtil(new TimestampUtil());
        tbUtilSpy = Mockito.spy(tbUtilTmpRef);

        mockedRepository = Mockito.mock(TrendBarRepository.class);
        Mockito.doNothing().when(mockedRepository).saveTrendBar(Mockito.any());
        Mockito.doNothing().when(mockedRepository).saveTrendBarList(Mockito.anyList());

        underTest = new TrendBarServiceImpl(mockedRepository, tbUtilSpy);
        currentTbMap = ((TrendBarServiceImpl) underTest).getCurrentTrendBars();
        lockMap = ((TrendBarServiceImpl) underTest).getSymbolLockMap();
    }

    @After
    public void destroy() {
        underTest = null;
    }

    @Test
    public void updateTrendBarNoCurrentTbForSymbol() {
        Quote qt = new Quote(BigDecimal.TEN, "AMDUSD", Instant.now());
        assertTrue(currentTbMap.isEmpty());
        assertTrue(lockMap.isEmpty());

        underTest.updateTrendBar(qt);

        assertFalse(currentTbMap.isEmpty());
        assertFalse(lockMap.isEmpty());
        assertEquals(1, lockMap.size());
        assertTrue(lockMap.containsKey("AMDUSD"));
        assertEquals(1, currentTbMap.size());
        assertTrue(currentTbMap.containsKey("AMDUSD"));
        assertNotNull(currentTbMap.get("AMDUSD"));
        assertEquals(TbPeriodType.values().length, currentTbMap.get("AMDUSD").size());
    }

    @Test
    public void updateTrendBarNewQuoteInsideTbTimePeriod() {
        String symbol = "AMDUSD";
        Instant instant = Instant.now();
        Quote qt = new Quote(BigDecimal.TEN, symbol, instant);
        underTest.updateTrendBar(qt);
        qt = new Quote(BigDecimal.ONE, symbol, instant.plusSeconds(1));

        TrendBar currentTbMinutePeriod = currentTbMap.get(symbol).get(TbPeriodType.M1);
        int identityHashBefore = System.identityHashCode(currentTbMap.get(symbol));
        underTest.updateTrendBar(qt);
        TrendBar updatedTbMinutePeriod = currentTbMap.get(symbol).get(TbPeriodType.M1);
        int identityHashAfter = System.identityHashCode(currentTbMap.get(symbol));

        assertNotEquals(identityHashBefore, identityHashAfter);
        assertNotEquals(currentTbMinutePeriod, updatedTbMinutePeriod);
        assertEquals(BigDecimal.ONE, updatedTbMinutePeriod.lowPrice());
        assertEquals(currentTbMinutePeriod.startTimestamp(), updatedTbMinutePeriod.startTimestamp());
        assertEquals(currentTbMinutePeriod.endTimestamp(), updatedTbMinutePeriod.endTimestamp());

        Mockito.verify(tbUtilSpy, Mockito.times(3)).copyWithUpdateLogic(Mockito.any(), Mockito.any());
        Mockito.verify(mockedRepository, Mockito.never()).saveTrendBar(Mockito.any());
    }

    @Test
    public void updateTrendBarNewQuoteAfterTbTimePeriod() {
        String symbol = "AMDUSD";
        Instant instant = Instant.now();
        Quote qt = new Quote(BigDecimal.TEN, symbol, instant);
        underTest.updateTrendBar(qt);
        qt = new Quote(BigDecimal.ONE, symbol, instant.plusSeconds(120));

        TrendBar currentTbMinutePeriod = currentTbMap.get(symbol).get(TbPeriodType.M1);
        int identityHashBefore = System.identityHashCode(currentTbMap.get(symbol));
        underTest.updateTrendBar(qt);
        TrendBar newTbMinutePeriod = currentTbMap.get(symbol).get(TbPeriodType.M1);
        int identityHashAfter = System.identityHashCode(currentTbMap.get(symbol));

        assertNotEquals(identityHashBefore, identityHashAfter);
        assertNotEquals(currentTbMinutePeriod, newTbMinutePeriod);
        assertEquals(BigDecimal.ONE, newTbMinutePeriod.lowPrice());
        assertEquals(BigDecimal.ONE, newTbMinutePeriod.highPrice());
        assertNotEquals(currentTbMinutePeriod.startTimestamp(), newTbMinutePeriod.startTimestamp());
        assertNotEquals(currentTbMinutePeriod.endTimestamp(), newTbMinutePeriod.endTimestamp());
        assertTrue(currentTbMinutePeriod.endTimestamp().isBefore(newTbMinutePeriod.startTimestamp()));

        Mockito.verify(tbUtilSpy, Mockito.times(2)).copyWithUpdateLogic(Mockito.any(), Mockito.any());
        Mockito.verify(tbUtilSpy, Mockito.atLeastOnce()).getNewTrendBarFromQuote(Mockito.any(), Mockito.eq(TbPeriodType.M1));
        Mockito.verify(mockedRepository, Mockito.atLeastOnce()).saveTrendBar(Mockito.any());
    }

    @Test
    public void getTrendBarHistoryByPeriodWhenNoHistory() {
        Mockito.doReturn(Optional.empty())
                .when(mockedRepository).getTbHistoryByPeriod(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());

        TbHistory actual = underTest.getTrendBarHistoryByPeriod("USDCNY", TbPeriodType.M1, Instant.now(), Instant.now());

        assertNotNull(actual);
        assertTrue(actual.trendBarSet().isEmpty());
        Mockito.verify(mockedRepository, Mockito.only()).getTbHistoryByPeriod(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void getTrendBarHistoryByPeriodWhenSuccess() {
        Optional<TbHistory> mockedResponse = Optional.of(new TbHistory(
                Set.of(
                    tbUtilSpy.getNewTrendBarFromQuote(new Quote(BigDecimal.TEN, "USDAMD", Instant.now()), TbPeriodType.M1),
                    tbUtilSpy.getNewTrendBarFromQuote(new Quote(BigDecimal.TEN, "USDKZT", Instant.now()), TbPeriodType.M1)
                )
        ));
        Mockito.doReturn(mockedResponse)
                .when(mockedRepository).getTbHistoryByPeriod(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());

        TbHistory actual = underTest.getTrendBarHistoryByPeriod("USDCNY", TbPeriodType.M1, Instant.now(), Instant.now());

        assertNotNull(actual);
        assertFalse(actual.trendBarSet().isEmpty());
        assertEquals(2, actual.trendBarSet().size());
        Mockito.verify(mockedRepository, Mockito.only()).getTbHistoryByPeriod(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
    }

}