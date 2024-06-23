package org.example.trendbar.worker;

import org.example.trendbar.model.Quote;
import org.example.trendbar.queue.QuoteQueueProvider;
import org.example.trendbar.service.TrendBarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author tuchnyak (George Shchennikov)
 */
@Component
public class QuoteQueueWorker implements InitializingBean, DisposableBean {

    private static final long WORKER_INITIAL_DELAY = 500;
    private static final long WORKER_DELAY = 3000;

    private final Logger logger = LoggerFactory.getLogger(QuoteQueueWorker.class);

    private final TrendBarService trendBarService;
    private final ScheduledExecutorService workerExecutor;
    private final QuoteQueueProvider queueProvider;

    public QuoteQueueWorker(TrendBarService trendBarService, ScheduledExecutorService workerExecutor, QuoteQueueProvider queueProvider) {
        this.trendBarService = trendBarService;
        this.workerExecutor = workerExecutor;
        this.queueProvider = queueProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        workerExecutor.scheduleWithFixedDelay(
                () -> {
                    logger.info(">>> Start queuing");
                    while (!queueProvider.isEmpty()) {
                        Optional<Quote> optionalQuote = queueProvider.dequeue();
                        if (optionalQuote.isPresent()) {
                            Quote quote = optionalQuote.get();
                            trendBarService.updateTrendBar(quote);
                            logger.info(">>> Quote [{}] has been updated", quote);
                        } else {
                            logger.debug(">>> Dequeued quote is empty");
                        }
                    }
                    logger.info(">>> End queuing, will sleep for: {}", WORKER_DELAY);
                },
                WORKER_INITIAL_DELAY,
                WORKER_DELAY,
                TimeUnit.MILLISECONDS
        );
        logger.info("Queue worker has been scheduled");
    }

    @Override
    public void destroy() throws Exception {
        logger.info(">>> Will destroy worker executor service");
        workerExecutor.shutdown();
        try {
            if (!workerExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                logger.info(">>> Executor service still in use");
                workerExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warn(">>> Error during shutting down of executor service", e);
            workerExecutor.shutdownNow();
        }
        logger.info(">>> Worker executor service has been destroyed");
    }

}
