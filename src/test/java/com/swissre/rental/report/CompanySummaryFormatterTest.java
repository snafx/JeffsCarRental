package com.swissre.rental.report;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.DailySummary;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanySummaryFormatterTest {

    private final CompanySummaryFormatter formatter = new CompanySummaryFormatter(LocalDate.of(2026, 2, 13));

    private CostBreakdown eVanBreakdown() {
        VehicleRental rental = new VehicleRental(VehicleType.E_VAN, new BigDecimal("95"), new BigDecimal("20"), true, 3, BigDecimal.ZERO);
        return new CostBreakdown(rental,
                new BigDecimal("64.60"), new BigDecimal("6.00"),
                new BigDecimal("9.00"), new BigDecimal("5.00"),
                new BigDecimal("0.00"), new BigDecimal("-10.00"),
                new BigDecimal("74.60"));
    }

    private CostBreakdown compactVanBreakdown() {
        VehicleRental rental = new VehicleRental(VehicleType.COMPACT_VAN, new BigDecimal("40"), new BigDecimal("5"), false, 0, BigDecimal.ZERO);
        return new CostBreakdown(rental,
                new BigDecimal("32.80"), new BigDecimal("9.75"),
                new BigDecimal("0.00"), new BigDecimal("0.00"),
                new BigDecimal("0.00"), new BigDecimal("0.00"),
                new BigDecimal("42.55"));
    }

    private CostBreakdown largeVanBreakdown() {
        VehicleRental rental = new VehicleRental(VehicleType.LARGE_VAN, new BigDecimal("180"), new BigDecimal("15"), false, 0, new BigDecimal("30"));
        return new CostBreakdown(rental,
                new BigDecimal("189.00"), new BigDecimal("29.25"),
                new BigDecimal("0.00"), new BigDecimal("0.00"),
                new BigDecimal("30.00"), new BigDecimal("0.00"),
                new BigDecimal("248.25"));
    }

    private DailySummary fullSummary() {
        return new DailySummary(
                List.of(eVanBreakdown(), compactVanBreakdown(), largeVanBreakdown()),
                new BigDecimal("365.40"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "JEFF'S CAR RENTAL",
            "Company Daily Summary",
            "13/02/2026",
            "Total vehicles: 3",
            "Company Revenue",
            "Electricity",
            "Fuel"
    })
    void formatSummary_containsExpectedText(String expected) {
        String output = formatter.formatSummary(fullSummary());
        assertTrue(output.contains(expected), "Expected output to contain: " + expected);
    }

    @Test
    void formatSummary_containsEVanHeader() {
        String output = formatter.formatSummary(fullSummary());
        assertTrue(output.contains("#1"));
        assertTrue(output.contains("E-Van"));
        assertTrue(output.contains("95 km"));
        assertTrue(output.contains("20 kWh"));
        assertTrue(output.contains("Vignette"));
        assertTrue(output.contains("Gubrist x3"));
    }

    @Test
    void formatSummary_containsCompactVanHeader() {
        String output = formatter.formatSummary(fullSummary());
        assertTrue(output.contains("#2"));
        assertTrue(output.contains("Compact Van"));
        assertTrue(output.contains("40 km"));
        assertTrue(output.contains("5 L"));
    }

    @Test
    void formatSummary_containsLargeVanHeader() {
        String output = formatter.formatSummary(fullSummary());
        assertTrue(output.contains("#3"));
        assertTrue(output.contains("Large Van"));
        assertTrue(output.contains("180 km"));
        assertTrue(output.contains("15 L"));
        assertTrue(output.contains("City 30 km"));
    }

    @Test
    void formatSummary_containsDistanceCharges() {
        String output = formatter.formatSummary(fullSummary());
        assertTrue(output.contains("64.60"));
        assertTrue(output.contains("32.80"));
        assertTrue(output.contains("189.00"));
    }

    @Test
    void formatSummary_containsSubtotals() {
        String output = formatter.formatSummary(fullSummary());
        assertTrue(output.contains("Subtotal"));
        assertTrue(output.contains("74.60"));
        assertTrue(output.contains("42.55"));
        assertTrue(output.contains("248.25"));
    }

    @ParameterizedTest
    @CsvSource({
            "GRAND TOTAL, 365.40",
            "Eco-bonus, -10.00",
            "City congestion, 30.00",
            "+--, +=="
    })
    void formatSummary_containsExpectedPair(String first, String second) {
        String output = formatter.formatSummary(fullSummary());
        assertTrue(output.contains(first.trim()), "Expected output to contain: " + first);
        assertTrue(output.contains(second.trim()), "Expected output to contain: " + second);
    }

    @Test
    void formatSummary_singleVehicle_vehicleCountIsOne() {
        DailySummary single = new DailySummary(List.of(eVanBreakdown()), new BigDecimal("74.60"));
        String output = formatter.formatSummary(single);
        assertTrue(output.contains("Total vehicles: 1"));
    }
}
