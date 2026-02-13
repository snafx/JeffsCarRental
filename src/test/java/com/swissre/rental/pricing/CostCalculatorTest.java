package com.swissre.rental.pricing;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.DailySummary;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CostCalculatorTest {

    private final CostCalculator calculator = new CostCalculator();

    // --- Distance cost ---

    @Test
    void distanceCostCompactVan() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("32.80"), result.distanceCost());
    }

    @Test
    void distanceCostLargeVan() {
        VehicleRental rental = rental(VehicleType.LARGE_VAN, "180", "15", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("189.00"), result.distanceCost());
    }

    @Test
    void distanceCostEVan() {
        VehicleRental rental = rental(VehicleType.E_VAN, "95", "20", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("64.60"), result.distanceCost());
    }

    // --- Energy cost ---

    @Test
    void fuelCostCompactVan() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("9.75"), result.energyCost());
    }

    @Test
    void fuelCostLargeVan() {
        VehicleRental rental = rental(VehicleType.LARGE_VAN, "180", "15", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("29.25"), result.energyCost());
    }

    @Test
    void electricityCostEVan() {
        VehicleRental rental = rental(VehicleType.E_VAN, "95", "20", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("6.00"), result.energyCost());
    }

    // --- Motorway vignette ---

    @Test
    void vignetteWhenTrue() {
        VehicleRental rental = rental(VehicleType.E_VAN, "95", "20", true, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("9.00"), result.vignetteCost());
    }

    @Test
    void vignetteWhenFalse() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.vignetteCost());
    }

    // --- Gubrist tunnel ---

    @Test
    void gubristZeroPassages() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.gubristCost());
    }

    @Test
    void gubristOnePassage() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 1, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("2.50"), result.gubristCost());
    }

    @Test
    void gubristTwoPassages() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 2, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("5.00"), result.gubristCost());
    }

    @Test
    void gubristThreePassagesCappedAtTwo() {
        VehicleRental rental = rental(VehicleType.E_VAN, "95", "20", true, 3, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("5.00"), result.gubristCost());
    }

    @Test
    void gubristTenPassagesCappedAtTwo() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 10, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("5.00"), result.gubristCost());
    }

    // --- City congestion fee ---

    @Test
    void congestionFeeZeroCityKm() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.congestionCost());
    }

    @Test
    void congestionFee30CityKm() {
        VehicleRental rental = rental(VehicleType.LARGE_VAN, "180", "15", false, 0, "30");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("30.00"), result.congestionCost());
    }

    @Test
    void congestionFeeFractionalKm() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "50", "5", false, 0, "12.5");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("12.50"), result.congestionCost());
    }

    // --- Eco-bonus ---

    @Test
    void ecoBonusEVanOver80KmUnder22KwhPer100() {
        // 95 km, 20 kWh -> 20/95*100 = 21.05 kWh/100km -> qualifies (<22)
        VehicleRental rental = rental(VehicleType.E_VAN, "95", "20", true, 3, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("-10.00"), result.ecoBonus());
    }

    @Test
    void noEcoBonusForCompactVan() {
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "100", "5", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.ecoBonus());
    }

    @Test
    void noEcoBonusForLargeVan() {
        VehicleRental rental = rental(VehicleType.LARGE_VAN, "100", "5", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.ecoBonus());
    }

    @Test
    void noEcoBonusEVanExactly80Km() {
        // 80 km is NOT strictly greater than 80
        VehicleRental rental = rental(VehicleType.E_VAN, "80", "15", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.ecoBonus());
    }

    @Test
    void noEcoBonusEVanUnder80Km() {
        VehicleRental rental = rental(VehicleType.E_VAN, "50", "10", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.ecoBonus());
    }

    @Test
    void noEcoBonusEVanExactly22KwhPer100() {
        // 100 km, 22 kWh -> 22/100*100 = 22.0 kWh/100km -> NOT strictly less than 22
        VehicleRental rental = rental(VehicleType.E_VAN, "100", "22", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.ecoBonus());
    }

    @Test
    void noEcoBonusEVanAbove22KwhPer100() {
        // 100 km, 25 kWh -> 25 kWh/100km
        VehicleRental rental = rental(VehicleType.E_VAN, "100", "25", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.ecoBonus());
    }

    @Test
    void ecoBonusEVanBarelyOver80KmBarelyUnder22() {
        // 80.01 km, 17.59 kWh -> 17.59/80.01*100 = 21.985... kWh/100km -> qualifies
        VehicleRental rental = rental(VehicleType.E_VAN, "80.01", "17.59", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("-10.00"), result.ecoBonus());
    }

    // --- Subtotal ---

    @Test
    void subtotalCompactVanSimple() {
        // 40 km, 5L, no extras: 32.80 + 9.75 = 42.55
        VehicleRental rental = rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("42.55"), result.subtotal());
    }

    @Test
    void subtotalLargeVanWithCity() {
        // 180 km, 15L, city 30: 189.00 + 29.25 + 30.00 = 248.25
        VehicleRental rental = rental(VehicleType.LARGE_VAN, "180", "15", false, 0, "30");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("248.25"), result.subtotal());
    }

    @Test
    void subtotalEVanFull() {
        // 95 km, 20 kWh, vignette, gubrist x3: 64.60 + 6.00 + 9.00 + 5.00 - 10.00 = 74.60
        VehicleRental rental = rental(VehicleType.E_VAN, "95", "20", true, 3, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("74.60"), result.subtotal());
    }

    // --- Daily summary ---

    @Test
    void dailySummaryGrandTotal() {
        List<VehicleRental> rentals = List.of(
                rental(VehicleType.E_VAN, "95", "20", true, 3, "0"),
                rental(VehicleType.COMPACT_VAN, "40", "5", false, 0, "0"),
                rental(VehicleType.LARGE_VAN, "180", "15", false, 0, "30")
        );
        DailySummary summary = calculator.calculateDailySummary(rentals);
        assertEquals(3, summary.breakdowns().size());
        assertEquals(new BigDecimal("365.40"), summary.grandTotal());
    }

    @Test
    void dailySummaryEmptyList() {
        DailySummary summary = calculator.calculateDailySummary(List.of());
        assertTrue(summary.breakdowns().isEmpty());
        assertEquals(new BigDecimal("0.00"), summary.grandTotal());
    }

    // --- Edge cases ---

    @Test
    void zeroKilometersZeroEnergy() {
        VehicleRental rental = rental(VehicleType.E_VAN, "0", "0", false, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.distanceCost());
        assertEquals(new BigDecimal("0.00"), result.energyCost());
        assertEquals(new BigDecimal("0.00"), result.ecoBonus());
        assertEquals(new BigDecimal("0.00"), result.subtotal());
    }

    @Test
    void zeroKmEVanNoEcoBonus() {
        VehicleRental rental = rental(VehicleType.E_VAN, "0", "0", true, 0, "0");
        CostBreakdown result = calculator.calculate(rental);
        assertEquals(new BigDecimal("0.00"), result.ecoBonus());
    }

    private VehicleRental rental(VehicleType type, String km, String energy,
                                 boolean vignette, int gubrist, String cityKm) {
        return new VehicleRental(type, new BigDecimal(km), new BigDecimal(energy),
                vignette, gubrist, new BigDecimal(cityKm));
    }
}
