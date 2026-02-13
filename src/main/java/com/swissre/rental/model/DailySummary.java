package com.swissre.rental.model;

import java.math.BigDecimal;
import java.util.List;

public record DailySummary(
        List<CostBreakdown> breakdowns,
        BigDecimal grandTotal
) {
}
