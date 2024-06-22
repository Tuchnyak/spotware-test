package org.example.trendbar;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class TrendBarConfigTest {

    private static ApplicationContext underTest;

    @BeforeClass
    public static void setUp() throws Exception {
        underTest = new AnnotationConfigApplicationContext(TrendBarConfig.class);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        underTest = null;
    }

    @Test
    public void testContext() {
        Object actual = underTest.getBean("trendBarConfig");

        assertNotNull(actual);
        assertTrue(actual instanceof TrendBarConfig);
    }

}