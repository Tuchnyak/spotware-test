package org.example.trendbar.queue;

import org.example.trendbar.model.Quote;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class QuoteQueueProviderImplTest {

    private static final Logger logger = LoggerFactory.getLogger(QuoteQueueProviderImplTest.class) ;

    private QuoteQueueProvider underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new QuoteQueueProviderImpl();
    }

    @After
    public void tearDown() throws Exception {
        underTest = null;
    }

    @Test
    public void enqueue() {
        var quote = new Quote(BigDecimal.TEN, "USDAMD", Instant.now());
        underTest.enqueue(quote);

        assertFalse(underTest.isEmpty());
        assertEquals(1, underTest.size());
    }

    @Test
    public void dequeue() {
        var quote = new Quote(BigDecimal.TEN, "USDAMD", Instant.now());
        underTest.enqueue(quote);

        assertFalse(underTest.isEmpty());
        assertEquals(1, underTest.size());

        Optional<Quote> actual = underTest.dequeue();
        assertTrue(underTest.isEmpty());
        assertEquals(0, underTest.size());
        assertTrue(actual.isPresent());
        assertEquals(quote, actual.get());
    }

    @Test
    public void size() {
        assertEquals(0, underTest.size());
    }

    @Test
    public void isEmpty() {
        assertTrue(underTest.isEmpty());
    }

    @Test
    public void concurrentEnqueuing() throws InterruptedException {
        Runnable runnable = () -> {
            String threadName = Thread.currentThread().getName();
            logger.info(">>> Thread {} begin", threadName);
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            underTest.enqueue(new Quote(BigDecimal.ONE, "USDAMD", Instant.now()));
            logger.info(">>> Thread {} enqueued", threadName);
        };

        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executor.submit(runnable);
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertFalse(underTest.isEmpty());
        assertEquals(5, underTest.size());
    }

}