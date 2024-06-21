package org.example.trendbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class MainTest {

    private static final Logger logger = LoggerFactory.getLogger(MainTest.class) ;

    public static void main(String[] args) {
        logger.info("Hello world! {}", Instant.now().atZone(ZoneId.systemDefault()));
    }

}
