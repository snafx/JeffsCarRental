package com.swissre.rental.pricing;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.DailySummary;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Stateless pricing engine — calculates distance, energy, vignette, Gubrist toll, city congestion, and eco-bonus.
 */
public class CostCalculator {

    private static final BigDecimal VIGNETTE_COST = new BigDecimal("9.00");
    private static final BigDecimal GUBRIST_COST_PER_PASSAGE = new BigDecimal("2.50");
    private static final int GUBRIST_MAX_PAID = 2;
    private static final BigDecimal CONGESTION_RATE_PER_KM = new BigDecimal("1.00");
    private static final BigDecimal ECO_BONUS_AMOUNT = new BigDecimal("-10.00");
    private static final BigDecimal ECO_BONUS_MIN_KM = new BigDecimal("80");
    private static final BigDecimal ECO_BONUS_MAX_KWH_PER_100 = new BigDecimal("22");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public CostBreakdown calculate(VehicleRental rental) {
        BigDecimal distanceCost = rental.kilometersDriven().multiply(rental.vehicleType().ratePerKm()).setScale(SCALE, ROUNDING);

        BigDecimal energyCost = rental.energyConsumed().multiply(rental.vehicleType().energyRate()).setScale(SCALE, ROUNDING);

        BigDecimal vignetteCost = rental.motorwayVignette() ? VIGNETTE_COST : BigDecimal.ZERO.setScale(SCALE, ROUNDING);

        int paidPassages = Math.min(rental.gubristTunnelPassages(), GUBRIST_MAX_PAID);
        BigDecimal gubristCost = GUBRIST_COST_PER_PASSAGE.multiply(BigDecimal.valueOf(paidPassages)).setScale(SCALE, ROUNDING);

        BigDecimal congestionCost = rental.cityKilometers().multiply(CONGESTION_RATE_PER_KM).setScale(SCALE, ROUNDING);

        BigDecimal ecoBonus = calculateEcoBonus(rental);

        BigDecimal subtotal = distanceCost
                .add(energyCost)
                .add(vignetteCost)
                .add(gubristCost)
                .add(congestionCost)
                .add(ecoBonus);

        return new CostBreakdown(rental, distanceCost, energyCost, vignetteCost, gubristCost, congestionCost, ecoBonus, subtotal);
    }

    public DailySummary calculateDailySummary(List<VehicleRental> rentals) {
        List<CostBreakdown> breakdowns = rentals.stream().map(this::calculate).toList();

        BigDecimal grandTotal = breakdowns.stream().map(CostBreakdown::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(SCALE, ROUNDING);

        return new DailySummary(breakdowns, grandTotal);
    }

    private BigDecimal calculateEcoBonus(VehicleRental rental) {
        if (rental.vehicleType() != VehicleType.E_VAN) {
            return BigDecimal.ZERO.setScale(SCALE, ROUNDING);
        }
        if (rental.kilometersDriven().compareTo(ECO_BONUS_MIN_KM) <= 0) {
            return BigDecimal.ZERO.setScale(SCALE, ROUNDING);
        }
        BigDecimal kwhPer100 = rental.energyConsumed().multiply(HUNDRED).divide(rental.kilometersDriven(), 10, ROUNDING);

        if (kwhPer100.compareTo(ECO_BONUS_MAX_KWH_PER_100) >= 0) {
            return BigDecimal.ZERO.setScale(SCALE, ROUNDING);
        }
        return ECO_BONUS_AMOUNT;
    }
}
