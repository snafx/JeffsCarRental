package com.swissre.rental.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRentalTest {

    @Test
    void validRentalIsCreated() {
        VehicleRental rental = new VehicleRental(
                VehicleType.E_VAN,
                new BigDecimal("95"),
                new BigDecimal("20"),
                true,
                3,
                BigDecimal.ZERO
        );
        assertEquals(VehicleType.E_VAN, rental.vehicleType());
        assertEquals(new BigDecimal("95"), rental.kilometersDriven());
        assertEquals(new BigDecimal("20"), rental.energyConsumed());
        assertTrue(rental.motorwayVignette());
        assertEquals(3, rental.gubristTunnelPassages());
        assertEquals(BigDecimal.ZERO, rental.cityKilometers());
    }

    @Test
    void negativeKilometersThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new VehicleRental(VehicleType.COMPACT_VAN, new BigDecimal("-1"),
                        BigDecimal.ZERO, false, 0, BigDecimal.ZERO));
    }

    @Test
    void negativeEnergyThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new VehicleRental(VehicleType.COMPACT_VAN, BigDecimal.TEN,
                        new BigDecimal("-5"), false, 0, BigDecimal.ZERO));
    }

    @Test
    void cityKilometersGreaterThanTotalThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new VehicleRental(VehicleType.LARGE_VAN, new BigDecimal("40"),
                        BigDecimal.TEN, false, 0, new BigDecimal("50")));
    }

    @Test
    void negativeCityKilometersThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new VehicleRental(VehicleType.LARGE_VAN, new BigDecimal("40"),
                        BigDecimal.TEN, false, 0, new BigDecimal("-1")));
    }

    @Test
    void negativePassagesThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new VehicleRental(VehicleType.COMPACT_VAN, BigDecimal.TEN,
                        BigDecimal.ZERO, false, -1, BigDecimal.ZERO));
    }

    @Test
    void nullVehicleTypeThrows() {
        assertThrows(NullPointerException.class, () ->
                new VehicleRental(null, BigDecimal.TEN,
                        BigDecimal.ZERO, false, 0, BigDecimal.ZERO));
    }

    @Test
    void zeroKilometersIsValid() {
        assertDoesNotThrow(() ->
                new VehicleRental(VehicleType.E_VAN, BigDecimal.ZERO,
                        BigDecimal.ZERO, false, 0, BigDecimal.ZERO));
    }

    @Test
    void cityKilometersEqualToTotalIsValid() {
        assertDoesNotThrow(() ->
                new VehicleRental(VehicleType.LARGE_VAN, new BigDecimal("30"),
                        BigDecimal.TEN, false, 0, new BigDecimal("30")));
    }
}
