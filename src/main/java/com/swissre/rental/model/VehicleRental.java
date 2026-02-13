package com.swissre.rental.model;

import java.math.BigDecimal;
import java.util.Objects;

public record VehicleRental(
        VehicleType vehicleType,
        BigDecimal kilometersDriven,
        BigDecimal energyConsumed,
        boolean motorwayVignette,
        int gubristTunnelPassages,
        BigDecimal cityKilometers
) {
    public VehicleRental {
        Objects.requireNonNull(vehicleType, "Vehicle Type must not be null");
        Objects.requireNonNull(kilometersDriven, "Kilometers Driven must not be null");
        Objects.requireNonNull(energyConsumed, "Energy Consumed must not be null");
        Objects.requireNonNull(cityKilometers, "City Kilometers must not be null");

        if (kilometersDriven.signum() < 0) {
            throw new IllegalArgumentException("Kilometers Driven must not be negative");
        }
        if (energyConsumed.signum() < 0) {
            throw new IllegalArgumentException("Energy Consumed must not be negative");
        }
        if (cityKilometers.signum() < 0) {
            throw new IllegalArgumentException("City Kilometers must not be negative");
        }
        if (cityKilometers.compareTo(kilometersDriven) > 0) {
            throw new IllegalArgumentException("City Kilometers must not exceed kilometersDriven");
        }
        if (gubristTunnelPassages < 0) {
            throw new IllegalArgumentException("Gubrist Tunnel Passages must not be negative");
        }
    }
}
