package org.example.trendbar.model;

import java.util.Set;

/**
 * @author tuchnyak (George Shchennikov)
 */
public record TbHistory(
        Set<TrendBar> trendBarSet
) {
}
