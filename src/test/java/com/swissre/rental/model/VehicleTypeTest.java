package com.swissre.rental.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTypeTest {

    @Test
    void compactVanHasCorrectRates() {
        VehicleType type = VehicleType.COMPACT_VAN;
        assertEquals(new BigDecimal("0.82"), type.ratePerKm());
        assertEquals(new BigDecimal("1.95"), type.energyRate());
        assertEquals("L", type.energyUnit());
        assertEquals("Compact Van", type.displayName());
    }

    @Test
    void largeVanHasCorrectRates() {
        VehicleType type = VehicleType.LARGE_VAN;
        assertEquals(new BigDecimal("1.05"), type.ratePerKm());
        assertEquals(new BigDecimal("1.95"), type.energyRate());
        assertEquals("L", type.energyUnit());
        assertEquals("Large Van", type.displayName());
    }

    @Test
    void eVanHasCorrectRates() {
        VehicleType type = VehicleType.E_VAN;
        assertEquals(new BigDecimal("0.68"), type.ratePerKm());
        assertEquals(new BigDecimal("0.30"), type.energyRate());
        assertEquals("kWh", type.energyUnit());
        assertEquals("E-Van", type.displayName());
    }

    @Test
    void exactlyThreeVehicleTypes() {
        assertEquals(3, VehicleType.values().length);
    }
}
