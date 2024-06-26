package org.example.demo;

import org.example.trendbar.TrendBarConfig;
import org.example.trendbar.model.Quote;
import org.example.trendbar.model.TbPeriodType;
import org.example.trendbar.model.TrendBar;
import org.example.trendbar.queue.QuoteQueueProvider;
import org.example.trendbar.repository.TrendBarRepository;
import org.example.trendbar.repository.TrendBarRepositoryStubImpl;
import org.example.trendbar.worker.QuoteQueueWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Just for simple demonstration
 *
 * @author tuchnyak (George Shchennikov)
 */
public class MainTest {

    private static final Logger logger = LoggerFactory.getLogger(MainTest.class) ;

    public static void main(String[] args) throws Exception {
        logger.info("Hello world! {}", Instant.now().atZone(ZoneId.systemDefault()));

        ApplicationContext ctx = new AnnotationConfigApplicationContext(TrendBarConfig.class);
        logger.info(">>> Config: {}", ctx.getBean("trendBarConfig"));
        QuoteQueueProvider queueProvider = ctx.getBean(QuoteQueueProvider.class);
        logger.info(">>> QueueProvider: {}", queueProvider);

        TrendBarRepository repository = ctx.getBean(TrendBarRepository.class);
        Map<TbPeriodType, List<TrendBar>> storage = ((TrendBarRepositoryStubImpl) repository).getStorage();
        logger.info("======>>>>> {}", storage);

        List<String> symboList = List.of("USDAMD", "CNYKZT", "RUBEUR", "EURAMD", "AMDRUB");
        int quoteNum = 1_000_000;
        IntStream.range(0, quoteNum).forEach(i -> {
            queueProvider.enqueue(
                    new Quote(
                            BigDecimal.valueOf(new SecureRandom().nextLong(1000L)),
                            symboList.get(new SecureRandom().nextInt(symboList.size())),
//                            Instant.now().plusSeconds(60 * new SecureRandom().nextInt(10))
                            Instant.now()
                    )
            );
        });

        for (int i = 0; i < 10; i++) {
            logger.info("======>>>>> {}", storage);
            TimeUnit.SECONDS.sleep(3);
        }

        QuoteQueueWorker worker = ctx.getBean(QuoteQueueWorker.class);
        worker.destroy();

        logger.info("======>>>>> {}", storage);
    }

}
