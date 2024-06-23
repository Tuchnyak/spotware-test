package org.example.trendbar.util;

import org.example.trendbar.model.TbPeriodType;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author tuchnyak (George Shchennikov)
 */
@Component
public class TimestampUtil {


    public Instant roundByPeriod(Instant initialTTimestamp, TbPeriodType periodType) {

        return initialTTimestamp.truncatedTo(periodType.getChronoUnit());
    }
}
