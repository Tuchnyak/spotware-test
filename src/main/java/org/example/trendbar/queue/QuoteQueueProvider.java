package org.example.trendbar.queue;

import org.example.trendbar.model.Quote;

import java.util.Optional;

/**
 * @author tuchnyak (George Shchennikov)
 */
public interface QuoteQueueProvider {

    void enqueue(Quote quote);
    Optional<Quote> dequeue();
    long size();
    boolean isEmpty();

}
