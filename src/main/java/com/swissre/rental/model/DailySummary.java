package com.swissre.rental.model;

import java.math.BigDecimal;
import java.util.List;

/** Aggregated daily report containing all rental breakdowns and the grand total. */
public record DailySummary(
        List<CostBreakdown> breakdowns,
        BigDecimal grandTotal
) {
}
