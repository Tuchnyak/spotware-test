package org.example.trendbar.model;

import java.time.temporal.ChronoUnit;

/**
 * @author tuchnyak (George Shchennikov)
 */
public enum TbPeriodType {
    M1(ChronoUnit.MINUTES),
    H1(ChronoUnit.HOURS),
    D1(ChronoUnit.DAYS);

    private final ChronoUnit chronoUnit;

    TbPeriodType(ChronoUnit chronoUnit) {
        this.chronoUnit = chronoUnit;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }

}
