package org.example.trendbar;

import org.example.trendbar.queue.QuoteQueueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Instant;
import java.time.ZoneId;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class MainTest {

    private static final Logger logger = LoggerFactory.getLogger(MainTest.class) ;

    public static void main(String[] args) {
        logger.info("Hello world! {}", Instant.now().atZone(ZoneId.systemDefault()));

        ApplicationContext ctx = new AnnotationConfigApplicationContext(TrendBarConfig.class);
        logger.info(">>> Config: {}", ctx.getBean("trendBarConfig"));
        logger.info(">>> QueueProvider: {}", ctx.getBean(QuoteQueueProvider.class));
    }

}
