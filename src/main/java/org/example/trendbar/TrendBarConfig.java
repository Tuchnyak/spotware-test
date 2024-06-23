package org.example.trendbar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author tuchnyak (George Shchennikov)
 */
@Configuration
@ComponentScan
public class TrendBarConfig {

    @Bean
    public ScheduledExecutorService workerExecutor() {
        CustomizableThreadFactory tFactory = new CustomizableThreadFactory("WORKER-THREAD-");
        tFactory.setThreadGroupName("QUEUE-WORKER-GROUP");
        tFactory.setThreadPriority(Thread.NORM_PRIORITY);

        return Executors.newSingleThreadScheduledExecutor(tFactory);
    }

}
