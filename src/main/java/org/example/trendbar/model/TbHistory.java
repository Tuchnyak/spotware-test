package org.example.trendbar.model;

import java.util.List;

/**
 * @author tuchnyak (George Shchennikov)
 */
public record TbHistory(
        List<TrendBar> trendBarList
) {
}
