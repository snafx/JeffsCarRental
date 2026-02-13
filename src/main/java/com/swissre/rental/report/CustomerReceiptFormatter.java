package com.swissre.rental.report;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerReceiptFormatter {

    private static final int WIDTH = 50;
    private static final String BORDER = "+" + "-".repeat(WIDTH) + "+";
    private static final String DOUBLE_BORDER = "+" + "=".repeat(WIDTH) + "+";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LocalDate date;

    public CustomerReceiptFormatter() {
        this(LocalDate.now());
    }

    public CustomerReceiptFormatter(LocalDate date) {
        this.date = date;
    }

    public String formatReceipt(CostBreakdown breakdown) {
        StringBuilder sb = new StringBuilder();
        VehicleRental rental = breakdown.rental();
        VehicleType type = rental.vehicleType();

        sb.append(BORDER).append("\n");
        sb.append(centerLine("JEFF'S CAR RENTAL")).append("\n");
        sb.append(centerLine("Customer Receipt")).append("\n");
        sb.append(DOUBLE_BORDER).append("\n");

        sb.append(leftLine("Date: " + date.format(DATE_FORMAT))).append("\n");
        sb.append(emptyLine()).append("\n");
        sb.append(leftLine("Vehicle:    " + type.displayName())).append("\n");
        sb.append(leftLine("Distance:   " + stripTrailingZeros(rental.kilometersDriven()) + " km")).append("\n");
        sb.append(leftLine("City km:    " + stripTrailingZeros(rental.cityKilometers()) + " km")).append("\n");
        sb.append(leftLine("Energy:     " + stripTrailingZeros(rental.energyConsumed()) + " " + type.energyUnit())).append("\n");
        sb.append(leftLine("Vignette:   " + (rental.motorwayVignette() ? "Yes" : "No"))).append("\n");
        sb.append(leftLine("Gubrist:    " + rental.gubristTunnelPassages() + " passages")).append("\n");

        sb.append(DOUBLE_BORDER).append("\n");
        sb.append(centerLine("CHARGES")).append("\n");
        sb.append(DOUBLE_BORDER).append("\n");

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
            sb.append(chargeLine("Zurich Congestion Fee", breakdown.congestionCost())).append("\n");
        }
        if (breakdown.ecoBonus().signum() != 0) {
            sb.append(chargeLine("Eco-bonus", breakdown.ecoBonus())).append("\n");
        }

        sb.append(DOUBLE_BORDER).append("\n");
        sb.append(chargeLine("TOTAL", breakdown.subtotal())).append("\n");
        sb.append(BORDER);

        return sb.toString();
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

    private String chargeLine(String label, BigDecimal amount) {
        String amountStr = String.format("CHF %12s", amount.toPlainString());
        int space = WIDTH - 2 - label.length() - amountStr.length();
        String line = label + " ".repeat(Math.max(1, space)) + amountStr;
        return "| " + String.format("%-" + (WIDTH - 2) + "s", line) + " |";
    }

    private String stripTrailingZeros(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
