package com.swissre.rental;

import com.swissre.rental.model.CostBreakdown;
import com.swissre.rental.model.DailySummary;
import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.pricing.CostCalculator;
import com.swissre.rental.report.CompanySummaryFormatter;
import com.swissre.rental.report.CustomerReceiptFormatter;
import com.swissre.rental.ui.ConsoleUI;

import com.swissre.rental.model.VehicleType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.swissre.rental.util.Constants.NO_RENTALS_ADDED_YET;

public class RentalSummaryApp {

    private final ConsoleUI ui;
    private final CostCalculator calculator;
    private final CustomerReceiptFormatter receiptFormatter;
    private final CompanySummaryFormatter summaryFormatter;
    private final List<VehicleRental> rentals;

    public RentalSummaryApp(ConsoleUI ui, CostCalculator calculator,
                            CustomerReceiptFormatter receiptFormatter,
                            CompanySummaryFormatter summaryFormatter) {
        this.ui = ui;
        this.calculator = calculator;
        this.receiptFormatter = receiptFormatter;
        this.summaryFormatter = summaryFormatter;
        this.rentals = new ArrayList<>();
    }

    public void run() {
        ui.displayBanner();

        boolean running = true;
        while (running) {
            ui.displayMenu();
            int choice = ui.getMenuChoice();

            switch (choice) {
                case 1 -> addRental();
                case 2 -> printCustomerReceipt();
                case 3 -> printCompanySummary();
                case 4 -> printTestCompanySummary();
                case 0 -> {
                    ui.displayMessage("Goodbye!");
                    running = false;
                }
            }
            ui.displayMessage("");
        }
    }

    private void addRental() {
        VehicleRental rental = ui.promptForRental();
        rentals.add(rental);
        ui.displayMessage("Rental added successfully. Total rentals: " + rentals.size());
    }

    private void printCustomerReceipt() {
        if (rentals.isEmpty()) {
            ui.displayError(NO_RENTALS_ADDED_YET);
            return;
        }

        int index = ui.selectRentalIndex(rentals);
        CostBreakdown breakdown = calculator.calculate(rentals.get(index));
        ui.displayMessage("");
        ui.displayMessage(receiptFormatter.formatReceipt(breakdown));
    }

    private void printCompanySummary() {
        if (rentals.isEmpty()) {
            ui.displayError(NO_RENTALS_ADDED_YET);
            return;
        }

        DailySummary summary = calculator.calculateDailySummary(rentals);
        ui.displayMessage("");
        ui.displayMessage(summaryFormatter.formatSummary(summary));
    }

    private void printTestCompanySummary() {
        List<VehicleRental> testRentals = List.of(
                new VehicleRental(VehicleType.E_VAN, new BigDecimal("95"), new BigDecimal("20"),
                        true, 3, BigDecimal.ZERO),
                new VehicleRental(VehicleType.COMPACT_VAN, new BigDecimal("40"), new BigDecimal("5"),
                        false, 0, BigDecimal.ZERO),
                new VehicleRental(VehicleType.LARGE_VAN, new BigDecimal("180"), new BigDecimal("15"),
                        false, 0, new BigDecimal("30"))
        );

        DailySummary summary = calculator.calculateDailySummary(testRentals);
        ui.displayMessage("");
        ui.displayMessage(summaryFormatter.formatSummary(summary));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUI ui = new ConsoleUI(scanner, System.out);
        CostCalculator calculator = new CostCalculator();
        CustomerReceiptFormatter receiptFormatter = new CustomerReceiptFormatter();
        CompanySummaryFormatter summaryFormatter = new CompanySummaryFormatter();

        RentalSummaryApp app = new RentalSummaryApp(ui, calculator, receiptFormatter, summaryFormatter);
        app.run();
    }
}
