package com.swissre.rental.report;

import com.swissre.rental.model.*;
import com.swissre.rental.pricing.CostCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SummaryFormatterTest {

    private final CostCalculator calculator = new CostCalculator();
    private final SummaryFormatter formatter = new SummaryFormatter();

    @Test
    void formatsEVanWithAllExtras() {
        VehicleRental rental = rental(VehicleType.E_VAN, "95", "20", true, 3, "0");
        CostBreakdown breakdown = calculator.calculate(rental);
        DailySummary summary = new DailySummary(List.of(breakdown), breakdown.subtotal());

        String output = formatter.format(summary);

        assertTrue(output.contains("E-Van"));
        assertTrue(output.contains("95"));
        assertTrue(output.contains("64.60"));
        assertTrue(output.contains("6.00"));
        assertTrue(output.contains("9.00"));
        assertTrue(output.contains("5.00"));
        assertTrue(output.contains("-10.00"));
        assertTrue(output.contains("74.60"));
    }

    @Test
    void formatsCompactVanSimple() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0");
        CostBreakdown breakdown = calculator.calculate(rental);
        DailySummary summary = new DailySummary(List.of(breakdown), breakdown.subtotal());

        String output = formatter.format(summary);

        assertTrue(output.contains("Compact Van"));
        assertTrue(output.contains("32.80"));
        assertTrue(output.contains("9.75"));
        assertTrue(output.contains("42.55"));
        assertFalse(output.contains("Vignette"));
        assertFalse(output.contains("Gubrist"));
        assertFalse(output.contains("Eco"));
    }

    @Test
    void formatsLargeVanWithCity() {
        VehicleRental rental = rental(VehicleType.LARGE_VAN, "180", "15", false, 0, "30");
        CostBreakdown breakdown = calculator.calculate(rental);
        DailySummary summary = new DailySummary(List.of(breakdown), breakdown.subtotal());

        String output = formatter.format(summary);

        assertTrue(output.contains("Large Van"));
        assertTrue(output.contains("189.00"));
        assertTrue(output.contains("29.25"));
        assertTrue(output.contains("30.00"));
        assertTrue(output.contains("248.25"));
    }

    @Test
    void formatsGrandTotal() {
        List<VehicleRental> rentals = List.of(
                rental(VehicleType.E_VAN, "95", "20", true, 3, "0"),
                rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0"),
                rental(VehicleType.LARGE_VAN, "180", "15", false, 0, "30")
        );
        DailySummary summary = calculator.calculateDailySummary(rentals);

        String output = formatter.format(summary);

        assertTrue(output.contains("Grand Total: CHF 365.40"));
    }

    @Test
    void formatsSeparatorBetweenVehicles() {
        List<VehicleRental> rentals = List.of(
                rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0"),
                rental(VehicleType.LARGE_VAN, "100", "10", false, 0, "0")
        );
        DailySummary summary = calculator.calculateDailySummary(rentals);

        String output = formatter.format(summary);

        assertTrue(output.contains("Compact Van"));
        assertTrue(output.contains("Large Van"));
    }

    @Test
    void emptyListProducesOnlyGrandTotal() {
        DailySummary summary = calculator.calculateDailySummary(List.of());
        String output = formatter.format(summary);
        assertTrue(output.contains("Grand Total: CHF 0.00"));
    }

    private VehicleRental rental(VehicleType type, String km, String energy,
                                  boolean vignette, int gubrist, String cityKm) {
        return new VehicleRental(type, new BigDecimal(km), new BigDecimal(energy),
                vignette, gubrist, new BigDecimal(cityKm));
    }
}
