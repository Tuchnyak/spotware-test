package org.example.trendbar.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author tuchnyak (George Shchennikov)
 */
public record Quote(
        BigDecimal newPrice,
        String symbol,
        Instant timestamp
) {
    public Quote {
        symbol = symbol.toUpperCase();
    }
}
