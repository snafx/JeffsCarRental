package com.swissre.rental.model;

import java.math.BigDecimal;

public enum VehicleType {

    COMPACT_VAN("0.82", "1.95", "L", "Compact Van"),
    LARGE_VAN("1.05", "1.95", "L", "Large Van"),
    E_VAN("0.68", "0.30", "kWh", "E-Van");

    private final BigDecimal ratePerKm;
    private final BigDecimal energyRate;
    private final String energyUnit;
    private final String displayName;

    VehicleType(String ratePerKm, String energyRate, String energyUnit, String displayName) {
        this.ratePerKm = new BigDecimal(ratePerKm);
        this.energyRate = new BigDecimal(energyRate);
        this.energyUnit = energyUnit;
        this.displayName = displayName;
    }

    public BigDecimal ratePerKm() {
        return ratePerKm;
    }

    public BigDecimal energyRate() {
        return energyRate;
    }

    public String energyUnit() {
        return energyUnit;
    }

    public String displayName() {
        return displayName;
    }
}
