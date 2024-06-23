package org.example.trendbar.worker;

import org.example.trendbar.TrendBarConfig;
import org.example.trendbar.model.Quote;
import org.example.trendbar.queue.QuoteQueueProvider;
import org.example.trendbar.service.TrendBarService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author tuchnyak (George Shchennikov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TrendBarConfig.class)
public class QuoteQueueWorkerTest {

    @Autowired
    private TrendBarService tbService;
    private TrendBarService tbServiceSpy;

    @Autowired
    private QuoteQueueProvider queueProvider;
    private QuoteQueueProvider queueProviderSpy;

    @Autowired
    private ScheduledExecutorService workerExecutor;

    private QuoteQueueWorker underTest;

    @Before
    public void setUp() throws Exception {
        tbServiceSpy = Mockito.spy(tbService);
        Mockito.doNothing().when(tbServiceSpy).updateTrendBar(Mockito.any());
        queueProviderSpy = Mockito.spy(queueProvider);
        underTest = new QuoteQueueWorker(tbServiceSpy, workerExecutor, queueProviderSpy);
        underTest.afterPropertiesSet();
    }

    @After
    public void tearDown() throws Exception {
        underTest.destroy();
    }

    @Test
    public void test() throws Exception {
        queueProvider.enqueue(new Quote(BigDecimal.TEN, "USDAMD", Instant.now()));
        TimeUnit.MILLISECONDS.sleep(4000);

        Mockito.verify(queueProviderSpy, Mockito.times(1)).dequeue();
        Mockito.verify(tbServiceSpy, Mockito.times(1)).updateTrendBar(Mockito.any());
    }

}