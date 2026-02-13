package com.swissre.rental.report;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.DailySummary;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;

import java.math.BigDecimal;

public class SummaryFormatter {

    public String format(DailySummary summary) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < summary.breakdowns().size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            formatBreakdown(sb, summary.breakdowns().get(i));
        }

        if (!summary.breakdowns().isEmpty()) {
            sb.append("\n");
        }
        sb.append("Grand Total: CHF ").append(summary.grandTotal().toPlainString());

        return sb.toString();
    }

    private void formatBreakdown(StringBuilder sb, CostBreakdown breakdown) {
        VehicleRental rental = breakdown.rental();
        VehicleType type = rental.vehicleType();

        sb.append(type.displayName())
                .append(", ").append(stripTrailingZeros(rental.kilometersDriven())).append(" km")
                .append(", ").append(stripTrailingZeros(rental.energyConsumed())).append(" ").append(type.energyUnit());
        if (rental.motorwayVignette()) {
            sb.append(", Vignette");
        }
        if (rental.gubristTunnelPassages() > 0) {
            sb.append(", Gubrist x").append(rental.gubristTunnelPassages());
        }
        if (rental.cityKilometers().signum() > 0) {
            sb.append(", City ").append(stripTrailingZeros(rental.cityKilometers())).append(" km");
        }
        sb.append(":\n");

        String energyLabel = type == VehicleType.E_VAN ? "Electricity" : "Fuel";
        sb.append("  Distance: ").append(breakdown.distanceCost().toPlainString());
        sb.append(" | ").append(energyLabel).append(": ").append(breakdown.energyCost().toPlainString());

        if (breakdown.vignetteCost().signum() > 0) {
            sb.append(" | Vignette: ").append(breakdown.vignetteCost().toPlainString());
        }
        if (breakdown.gubristCost().signum() > 0) {
            sb.append(" | Gubrist: ").append(breakdown.gubristCost().toPlainString());
        }
        if (breakdown.congestionCost().signum() > 0) {
            sb.append(" | City: ").append(breakdown.congestionCost().toPlainString());
        }
        if (breakdown.ecoBonus().signum() != 0) {
            sb.append(" | Eco-bonus: ").append(breakdown.ecoBonus().toPlainString());
        }
        sb.append("\n");

        sb.append("  Subtotal: CHF ").append(breakdown.subtotal().toPlainString()).append("\n");
    }

    private String stripTrailingZeros(BigDecimal value) {
        BigDecimal stripped = value.stripTrailingZeros();
        return stripped.toPlainString();
    }
}
