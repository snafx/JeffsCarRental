package com.swissre.rental;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.DailySummary;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;
import com.swissre.rental.pricing.CostCalculator;
import com.swissre.rental.report.SummaryFormatter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RentalSummaryIntegrationTest {

    private final CostCalculator calculator = new CostCalculator();
    private final SummaryFormatter formatter = new SummaryFormatter();

    @Test
    void fullPdfExampleProducesCorrectGrandTotal() {
        List<VehicleRental> rentals = pdfExampleRentals();
        DailySummary summary = calculator.calculateDailySummary(rentals);

        assertEquals(new BigDecimal("365.40"), summary.grandTotal());
    }

    @Test
    void fullPdfExampleSubtotals() {
        List<VehicleRental> rentals = pdfExampleRentals();
        DailySummary summary = calculator.calculateDailySummary(rentals);

        assertEquals(new BigDecimal("74.60"), summary.breakdowns().get(0).subtotal());
        assertEquals(new BigDecimal("42.55"), summary.breakdowns().get(1).subtotal());
        assertEquals(new BigDecimal("248.25"), summary.breakdowns().get(2).subtotal());
    }

    @Test
    void fullPdfExampleEVanBreakdown() {
        List<VehicleRental> rentals = pdfExampleRentals();
        DailySummary summary = calculator.calculateDailySummary(rentals);
        CostBreakdown eVan = summary.breakdowns().getFirst();

        assertEquals(new BigDecimal("64.60"), eVan.distanceCost());
        assertEquals(new BigDecimal("6.00"), eVan.energyCost());
        assertEquals(new BigDecimal("9.00"), eVan.vignetteCost());
        assertEquals(new BigDecimal("5.00"), eVan.gubristCost());
        assertEquals(new BigDecimal("0.00"), eVan.congestionCost());
        assertEquals(new BigDecimal("-10.00"), eVan.ecoBonus());
    }

    @Test
    void fullPdfExampleFormattedOutputContainsKeyValues() {
        List<VehicleRental> rentals = pdfExampleRentals();
        DailySummary summary = calculator.calculateDailySummary(rentals);
        String output = formatter.format(summary);

        assertTrue(output.contains("Grand Total: CHF 365.40"));
        assertTrue(output.contains("E-Van"));
        assertTrue(output.contains("Compact Van"));
        assertTrue(output.contains("Large Van"));
    }

    private List<VehicleRental> pdfExampleRentals() {
        return List.of(
                new VehicleRental(VehicleType.E_VAN, new BigDecimal("95"), new BigDecimal("20"), true, 3, BigDecimal.ZERO),
                new VehicleRental(VehicleType.COMPACT_VAN, new BigDecimal("40"), new BigDecimal("5"), false, 0, BigDecimal.ZERO),
                new VehicleRental(VehicleType.LARGE_VAN, new BigDecimal("180"), new BigDecimal("15"), false, 0, new BigDecimal("30"))
        );
    }
}
