package com.swissre.rental.model;

import java.math.BigDecimal;

/** Itemized cost result for a single rental, including all charge components and subtotal. */
public record CostBreakdown(
        VehicleRental rental,
        BigDecimal distanceCost,
        BigDecimal energyCost,
        BigDecimal vignetteCost,
        BigDecimal gubristCost,
        BigDecimal congestionCost,
        BigDecimal ecoBonus,
        BigDecimal subtotal
) {
}
