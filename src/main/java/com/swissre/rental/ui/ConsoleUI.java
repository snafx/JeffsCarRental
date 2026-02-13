package com.swissre.rental.ui;

import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import static com.swissre.rental.util.Constants.*;

public class ConsoleUI {

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleUI(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    public void displayBanner() {
        out.println();
        out.println(SEPARATION_LINE_EQUALS_SIGN);
        out.println("|            Jeff's Car Rental                   |");
        out.println(SEPARATION_LINE_EQUALS_SIGN);
        out.println();
    }

    public void displayMenu() {
        String border = SEPARATION_LINE_EQUALS_SIGN;
        out.println(border);
        out.printf(DISPLAY_MENU_FORMATTING_PATTERN, "Please select an option:");
        out.println(border);
        out.printf(DISPLAY_MENU_FORMATTING_PATTERN, "");
        out.printf(DISPLAY_MENU_FORMATTING_PATTERN, "1 - Add rental vehicle summary");
        out.printf(DISPLAY_MENU_FORMATTING_PATTERN, "2 - Print customer receipt");
        out.printf(DISPLAY_MENU_FORMATTING_PATTERN, "3 - Print company daily summary");
        out.printf(DISPLAY_MENU_FORMATTING_PATTERN, "0 - Exit");
        out.printf(DISPLAY_MENU_FORMATTING_PATTERN, "");
        out.println(border);
        out.println();
    }

    public int getMenuChoice() {
        while (true) {
            out.print("Choose an option: ");
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 0 && choice <= 3) {
                    return choice;
                }
                out.println("Invalid option. Please enter 0, 1, 2, or 3.");
            } catch (NumberFormatException e) {
                out.println(INVALID_INPUT_PLEASE_ENTER_A_NUMBER);
            }
        }
    }

    public VehicleRental promptForRental() {
        out.println();
        out.println("--- Add New Rental ---");

        VehicleType vehicleType = promptVehicleType();
        BigDecimal kilometers = promptBigDecimal("Kilometers driven: ");
        BigDecimal energy = promptBigDecimal("Energy consumed (" + vehicleType.energyUnit() + "): ");
        boolean vignette = promptYesNo("Motorway vignette? (y/n): ");
        int gubrist = promptInt("Gubrist tunnel passages: ");
        BigDecimal cityKm = promptCityKilometers(kilometers);

        return new VehicleRental(vehicleType, kilometers, energy, vignette, gubrist, cityKm);
    }

    public int selectRentalIndex(List<VehicleRental> rentals) {
        out.println();
        out.println("--- Select Rental ---");
        for (int i = 0; i < rentals.size(); i++) {
            VehicleRental r = rentals.get(i);
            out.printf("%d - %s, %s km%n", i + 1, r.vehicleType().displayName(),
                    stripTrailingZeros(r.kilometersDriven()));
        }
        out.println();

        while (true) {
            out.print("Select rental number: ");
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= rentals.size()) {
                    return choice - 1;
                }
                out.println("Invalid selection. Enter a number between 1 and " + rentals.size() + ".");
            } catch (NumberFormatException e) {
                out.println(INVALID_INPUT_PLEASE_ENTER_A_NUMBER);
            }
        }
    }

    public void displayMessage(String message) {
        out.println(message);
    }

    public void displayError(String message) {
        out.println("Error: " + message);
    }

    private VehicleType promptVehicleType() {
        VehicleType[] types = VehicleType.values();
        out.println("Vehicle type:");
        for (int i = 0; i < types.length; i++) {
            out.printf("  %d - %s%n", i + 1, types[i].displayName());
        }

        while (true) {
            out.print("Choose vehicle type: ");
            String line = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= types.length) {
                    return types[choice - 1];
                }
                out.println("Invalid selection. Enter a number between 1 and " + types.length + ".");
            } catch (NumberFormatException e) {
                out.println(INVALID_INPUT_PLEASE_ENTER_A_NUMBER);
            }
        }
    }

    private BigDecimal promptCityKilometers(BigDecimal totalKilometers) {
        while (true) {
            BigDecimal cityKm = promptBigDecimal("City kilometers: ");
            if (cityKm.compareTo(totalKilometers) <= 0) {
                return cityKm;
            }
            out.println("City Kilometers must not exceed total kilometers (" + stripTrailingZeros(totalKilometers) + " km).");
        }
    }

    private BigDecimal promptBigDecimal(String prompt) {
        while (true) {
            out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                BigDecimal value = new BigDecimal(line);
                if (value.signum() < 0) {
                    out.println("Value must not be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                out.println("Invalid number. Please try again.");
            }
        }
    }

    private boolean promptYesNo(String prompt) {
        while (true) {
            out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) {
                return true;
            }
            if (line.equals("n") || line.equals("no")) {
                return false;
            }
            out.println("Please enter 'y' or 'n'.");
        }
    }

    private int promptInt(String prompt) {
        while (true) {
            out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < 0) {
                    out.println("Value must not be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                out.println("Invalid number. Please try again.");
            }
        }
    }

    private String stripTrailingZeros(BigDecimal value) {
        BigDecimal stripped = value.stripTrailingZeros();
        return stripped.toPlainString();
    }
}
