package org.example.trendbar.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @author tuchnyak (George Shchennikov)
 */
public class TrendBar {

    private final BigDecimal openPrice;
    private final BigDecimal closePrice;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final TbPeriodType periodType;
    private final Instant startTimestamp;
    private final Instant endTimestamp;

    public TrendBar(BigDecimal openPrice, BigDecimal closePrice, BigDecimal highPrice, BigDecimal lowPrice, TbPeriodType periodType, Instant startTimestamp) {
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.periodType = periodType;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = startTimestamp.plus(1, periodType.getChronoUnit());
    }

    public static class Builder {
        private BigDecimal openPrice;
        private BigDecimal closePrice;
        private BigDecimal highPrice;
        private BigDecimal lowPrice;
        private TbPeriodType periodType;
        private Instant startTimestamp;

        public Builder openPrice(BigDecimal openPrice) {
            this.openPrice = openPrice;
            return this;
        }

        public Builder closePrice(BigDecimal closePrice) {
            this.closePrice = closePrice;
            return this;
        }

        public Builder highPrice(BigDecimal highPrice) {
            this.highPrice = highPrice;
            return this;
        }

        public Builder lowPrice(BigDecimal lowPrice) {
            this.lowPrice = lowPrice;
            return this;
        }

        public Builder periodType(TbPeriodType periodType) {
            this.periodType = periodType;
            return this;
        }

        public Builder startTimestamp(Instant startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public TrendBar build() {

            return new TrendBar(openPrice, closePrice, highPrice, lowPrice, periodType, startTimestamp);
        }

    }

    // BOILERPLATE CODE
    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public TbPeriodType getPeriodType() {
        return periodType;
    }

    public Instant getStartTimestamp() {
        return startTimestamp;
    }

    public Instant getEndTimestamp() {
        return endTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrendBar trendBar = (TrendBar) o;
        return Objects.equals(getOpenPrice(), trendBar.getOpenPrice()) && Objects.equals(getClosePrice(), trendBar.getClosePrice()) && Objects.equals(getHighPrice(), trendBar.getHighPrice()) && Objects.equals(getLowPrice(), trendBar.getLowPrice()) && getPeriodType() == trendBar.getPeriodType() && Objects.equals(getStartTimestamp(), trendBar.getStartTimestamp()) && Objects.equals(getEndTimestamp(), trendBar.getEndTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOpenPrice(), getClosePrice(), getHighPrice(), getLowPrice(), getPeriodType(), getStartTimestamp(), getEndTimestamp());
    }

    @Override
    public String toString() {
        return "TrendBar{" +
                "openPrice=" + openPrice +
                ", closePrice=" + closePrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", periodType=" + periodType +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                '}';
    }

}
