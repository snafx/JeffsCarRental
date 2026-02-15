package com.swissre.rental.report;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.DailySummary;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formats an ASCII wide-table company daily summary from a {@link DailySummary}.
 *
 * <p>Lists every rental with a vehicle header, itemized charges, and subtotal,
 * followed by the grand total. Accepts {@link LocalDate} in the constructor
 * so tests can pin the date for deterministic output.</p>
 */
public class CompanySummaryFormatter {

    private static final int WIDTH = 118;
    private static final String BORDER = "+" + "-".repeat(WIDTH) + "+";
    private static final String DOUBLE_BORDER = "+" + "=".repeat(WIDTH) + "+";
    private static final String SEPARATOR = "|" + "-".repeat(WIDTH) + "|";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LocalDate date;

    public CompanySummaryFormatter() {
        this(LocalDate.now());
    }

    public CompanySummaryFormatter(LocalDate date) {
        this.date = date;
    }

    public String formatSummary(DailySummary summary) {
        StringBuilder sb = new StringBuilder();
        List<CostBreakdown> breakdowns = summary.breakdowns();

        sb.append(BORDER).append("\n");
        sb.append(centerLine("JEFF'S CAR RENTAL")).append("\n");
        sb.append(centerLine("Company Daily Summary")).append("\n");
        sb.append(DOUBLE_BORDER).append("\n");

        String dateStr = "Date: " + date.format(DATE_FORMAT);
        String vehicleCount = "Total vehicles: " + breakdowns.size();
        sb.append(twoColumnLine(dateStr, vehicleCount)).append("\n");

        sb.append(DOUBLE_BORDER).append("\n");

        for (int i = 0; i < breakdowns.size(); i++) {
            CostBreakdown breakdown = breakdowns.get(i);
            sb.append(emptyLine()).append("\n");
            sb.append(vehicleHeaderLine(i + 1, breakdown)).append("\n");
            sb.append(SEPARATOR).append("\n");
            appendCharges(sb, breakdown);
            sb.append(subtotalSeparatorLine()).append("\n");
            sb.append(chargeLine("Subtotal", breakdown.subtotal())).append("\n");
        }

        sb.append(DOUBLE_BORDER).append("\n");
        sb.append(chargeLine("GRAND TOTAL (Company Revenue)", summary.grandTotal())).append("\n");
        sb.append(BORDER);

        return sb.toString();
    }

    private void appendCharges(StringBuilder sb, CostBreakdown breakdown) {
        VehicleType type = breakdown.rental().vehicleType();
        String energyLabel = type == VehicleType.E_VAN ? "Electricity" : "Fuel";

        sb.append(chargeLine("Distance charge", breakdown.distanceCost())).append("\n");
        sb.append(chargeLine(energyLabel, breakdown.energyCost())).append("\n");

        if (breakdown.vignetteCost().signum() > 0) {
            sb.append(chargeLine("Motorway vignette", breakdown.vignetteCost())).append("\n");
        }
        if (breakdown.gubristCost().signum() > 0) {
            sb.append(chargeLine("Gubrist toll", breakdown.gubristCost())).append("\n");
        }
        if (breakdown.congestionCost().signum() > 0) {
            sb.append(chargeLine("City congestion", breakdown.congestionCost())).append("\n");
        }
        if (breakdown.ecoBonus().signum() != 0) {
            sb.append(chargeLine("Eco-bonus", breakdown.ecoBonus())).append("\n");
        }
    }

    private String vehicleHeaderLine(int index, CostBreakdown breakdown) {
        VehicleRental rental = breakdown.rental();
        VehicleType type = rental.vehicleType();

        StringBuilder desc = new StringBuilder();
        desc.append("#").append(index).append("  ").append(type.displayName());
        desc.append(" | ").append(stripTrailingZeros(rental.kilometersDriven())).append(" km");
        desc.append(" | ").append(stripTrailingZeros(rental.energyConsumed())).append(" ").append(type.energyUnit());

        if (rental.motorwayVignette()) {
            desc.append(" | Vignette");
        }
        if (rental.gubristTunnelPassages() > 0) {
            desc.append(" | Gubrist x").append(rental.gubristTunnelPassages());
        }
        if (rental.cityKilometers().signum() > 0) {
            desc.append(" | City ").append(stripTrailingZeros(rental.cityKilometers())).append(" km");
        }

        return leftLine(desc.toString());
    }

    private String centerLine(String text) {
        int padding = (WIDTH - text.length()) / 2;
        String padded = " ".repeat(Math.max(0, padding)) + text;
        return "| " + String.format("%-" + (WIDTH - 2) + "s", padded) + " |";
    }

    private String leftLine(String text) {
        return "| " + String.format("%-" + (WIDTH - 2) + "s", text) + " |";
    }

    private String emptyLine() {
        return "| " + " ".repeat(WIDTH - 2) + " |";
    }

    private String twoColumnLine(String left, String right) {
        int space = WIDTH - 2 - left.length() - right.length();
        String line = left + " ".repeat(Math.max(1, space)) + right;
        return "| " + String.format("%-" + (WIDTH - 2) + "s", line) + " |";
    }

    private String chargeLine(String label, BigDecimal amount) {
        String amountStr = String.format("CHF %12s", amount.toPlainString());
        String indentedLabel = "  " + label;
        int space = WIDTH - 2 - indentedLabel.length() - amountStr.length();
        String line = indentedLabel + " ".repeat(Math.max(1, space)) + amountStr;
        return "| " + String.format("%-" + (WIDTH - 2) + "s", line) + " |";
    }

    private String subtotalSeparatorLine() {
        String sep = "---------------";
        int padLeft = WIDTH - 2 - sep.length() - 1;
        return "| " + " ".repeat(padLeft) + sep + "  |";
    }

    private String stripTrailingZeros(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
