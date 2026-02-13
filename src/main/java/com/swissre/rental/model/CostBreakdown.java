package com.swissre.rental.model;

import java.math.BigDecimal;

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
