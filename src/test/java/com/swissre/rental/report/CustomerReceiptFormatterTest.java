package com.swissre.rental.report;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerReceiptFormatterTest {

    private final CustomerReceiptFormatter formatter =
            new CustomerReceiptFormatter(LocalDate.of(2026, 2, 13));

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

    @ParameterizedTest
    @ValueSource(strings = {
            "JEFF'S CAR RENTAL",
            "13/02/2026",
            "E-Van",
            "95 km",
            "20 kWh",
            "Vignette:   Yes",
            "3 passages",
            "Customer Receipt",
            "CHF",
            "City km:"
    })
    void formatReceipt_eVan_containsExpectedText(String expected) {
        String receipt = formatter.formatReceipt(eVanBreakdown());
        assertTrue(receipt.contains(expected), "Expected to contain: " + expected);
    }

    @ParameterizedTest
    @CsvSource({
            "Distance charge, 64.60",
            "Electricity, 6.00",
            "Eco-bonus, -10.00",
            "TOTAL, 74.60",
            "Motorway vignette, 9.00",
            "Gubrist toll, 5.00",
            "+--, +=="
    })
    void formatReceipt_eVan_containsExpectedPair(String first, String second) {
        String receipt = formatter.formatReceipt(eVanBreakdown());
        assertTrue(receipt.contains(first.trim()), "Expected to contain: " + first);
        assertTrue(receipt.contains(second.trim()), "Expected to contain: " + second);
    }

    @Test
    void formatReceipt_compactVan_usesFuelLabel() {
        String receipt = formatter.formatReceipt(compactVanBreakdown());
        assertTrue(receipt.contains("Fuel"));
        assertFalse(receipt.contains("Electricity"));
    }

    @Test
    void formatReceipt_compactVan_noVignetteNoGubrist() {
        String receipt = formatter.formatReceipt(compactVanBreakdown());
        assertFalse(receipt.contains("Motorway vignette"));
        assertFalse(receipt.contains("Gubrist toll"));
    }

    @Test
    void formatReceipt_compactVan_noEcoBonus() {
        String receipt = formatter.formatReceipt(compactVanBreakdown());
        assertFalse(receipt.contains("Eco-bonus"));
    }

    @Test
    void formatReceipt_withCityKm_showsZurichCongestionFee() {
        VehicleRental rental = new VehicleRental(VehicleType.LARGE_VAN, new BigDecimal("180"), new BigDecimal("15"), false, 0, new BigDecimal("30"));
        CostBreakdown breakdown = new CostBreakdown(rental,
                new BigDecimal("189.00"), new BigDecimal("29.25"),
                new BigDecimal("0.00"), new BigDecimal("0.00"),
                new BigDecimal("30.00"), new BigDecimal("0.00"),
                new BigDecimal("248.25"));

        String receipt = formatter.formatReceipt(breakdown);
        assertTrue(receipt.contains("Zurich Congestion Fee"));
        assertTrue(receipt.contains("30 km"));
        assertFalse(receipt.contains("City congestion"));
    }
}
