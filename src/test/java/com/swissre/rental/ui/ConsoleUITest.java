package com.swissre.rental.ui;

import com.swissre.rental.model.VehicleRental;
import com.swissre.rental.model.VehicleType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleUITest {

    private ConsoleUI createUI(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new ConsoleUI(scanner, new PrintStream(new ByteArrayOutputStream()));
    }

    private record UIWithOutput(ConsoleUI ui, ByteArrayOutputStream output) {}

    private UIWithOutput createUIWithOutput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return new UIWithOutput(new ConsoleUI(scanner, new PrintStream(baos)), baos);
    }

    @Test
    void getMenuChoice_validChoice_returnsChoice() {
        ConsoleUI ui = createUI("1\n");
        assertEquals(1, ui.getMenuChoice());
    }

    @Test
    void getMenuChoice_zero_returnsZero() {
        ConsoleUI ui = createUI("0\n");
        assertEquals(0, ui.getMenuChoice());
    }

    @Test
    void getMenuChoice_invalidThenValid_returnsValid() {
        ConsoleUI ui = createUI("5\nabc\n2\n");
        assertEquals(2, ui.getMenuChoice());
    }

    @Test
    void promptForRental_eVanFullInput_createsRental() {
        String input = "3\n95\n20\ny\n3\n0\n";
        ConsoleUI ui = createUI(input);

        VehicleRental rental = ui.promptForRental();

        assertEquals(VehicleType.E_VAN, rental.vehicleType());
        assertEquals(new BigDecimal("95"), rental.kilometersDriven());
        assertEquals(new BigDecimal("20"), rental.energyConsumed());
        assertTrue(rental.motorwayVignette());
        assertEquals(3, rental.gubristTunnelPassages());
        assertEquals(BigDecimal.ZERO, rental.cityKilometers());
    }

    @Test
    void promptForRental_compactVanNoExtras_createsRental() {
        String input = "1\n40\n5\nn\n0\n0\n";
        ConsoleUI ui = createUI(input);

        VehicleRental rental = ui.promptForRental();

        assertEquals(VehicleType.COMPACT_VAN, rental.vehicleType());
        assertEquals(new BigDecimal("40"), rental.kilometersDriven());
        assertEquals(new BigDecimal("5"), rental.energyConsumed());
        assertFalse(rental.motorwayVignette());
        assertEquals(0, rental.gubristTunnelPassages());
    }

    @Test
    void promptForRental_invalidVehicleTypeThenValid_recovers() {
        String input = "9\nabc\n2\n180\n15\nn\n0\n30\n";
        ConsoleUI ui = createUI(input);

        VehicleRental rental = ui.promptForRental();

        assertEquals(VehicleType.LARGE_VAN, rental.vehicleType());
        assertEquals(new BigDecimal("180"), rental.kilometersDriven());
    }

    @Test
    void promptForRental_negativeKmThenValid_recovers() {
        String input = "1\n-5\n40\n5\nn\n0\n0\n";
        ConsoleUI ui = createUI(input);

        VehicleRental rental = ui.promptForRental();

        assertEquals(new BigDecimal("40"), rental.kilometersDriven());
    }

    @Test
    void selectRentalIndex_validSelection_returnsIndex() {
        List<VehicleRental> rentals = List.of(
                new VehicleRental(VehicleType.E_VAN, new BigDecimal("95"), new BigDecimal("20"), true, 3, BigDecimal.ZERO),
                new VehicleRental(VehicleType.COMPACT_VAN, new BigDecimal("40"), new BigDecimal("5"), false, 0, BigDecimal.ZERO)
        );

        ConsoleUI ui = createUI("2\n");
        assertEquals(1, ui.selectRentalIndex(rentals));
    }

    @Test
    void selectRentalIndex_invalidThenValid_returnsValid() {
        List<VehicleRental> rentals = List.of(
                new VehicleRental(VehicleType.E_VAN, new BigDecimal("95"), new BigDecimal("20"), true, 3, BigDecimal.ZERO)
        );

        ConsoleUI ui = createUI("0\n5\nabc\n1\n");
        assertEquals(0, ui.selectRentalIndex(rentals));
    }

    @Test
    void displayBanner_containsCompanyName() {
        UIWithOutput uiOut = createUIWithOutput("");
        uiOut.ui().displayBanner();
        String output = uiOut.output().toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Jeff's Car Rental"));
    }

    @Test
    void displayMenu_containsAllOptions() {
        UIWithOutput uiOut = createUIWithOutput("");
        uiOut.ui().displayMenu();
        String output = uiOut.output().toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("1 - Add rental vehicle summary"));
        assertTrue(output.contains("2 - Print customer receipt"));
        assertTrue(output.contains("3 - Print company daily summary"));
        assertTrue(output.contains("0 - Exit"));
    }

    @Test
    void displayMessage_printsMessage() {
        UIWithOutput uiOut = createUIWithOutput("");
        uiOut.ui().displayMessage("Hello");
        String output = uiOut.output().toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hello"));
    }

    @Test
    void displayError_printsWithPrefix() {
        UIWithOutput uiOut = createUIWithOutput("");
        uiOut.ui().displayError("Something broke");
        String output = uiOut.output().toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Error: Something broke"));
    }

    @Test
    void promptForRental_yesNoAcceptsYes() {
        String input = "1\n100\n10\nyes\n0\n0\n";
        ConsoleUI ui = createUI(input);

        VehicleRental rental = ui.promptForRental();
        assertTrue(rental.motorwayVignette());
    }

    @Test
    void promptForRental_yesNoAcceptsNo() {
        String input = "1\n100\n10\nno\n0\n0\n";
        ConsoleUI ui = createUI(input);

        VehicleRental rental = ui.promptForRental();
        assertFalse(rental.motorwayVignette());
    }

    @Test
    void promptForRental_invalidYesNoThenValid() {
        String input = "1\n100\n10\nmaybe\ny\n0\n0\n";
        ConsoleUI ui = createUI(input);

        VehicleRental rental = ui.promptForRental();
        assertTrue(rental.motorwayVignette());
    }

    @Test
    void promptForRental_cityKmExceedsTotalKm_rePromptsOnlyCityKm() {
        String input = "1\n40\n5\nn\n0\n50\n10\n";
        UIWithOutput uiOut = createUIWithOutput(input);

        VehicleRental rental = uiOut.ui().promptForRental();

        assertEquals(new BigDecimal("40"), rental.kilometersDriven());
        assertEquals(new BigDecimal("10"), rental.cityKilometers());
        String output = uiOut.output().toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("must not exceed"));
    }
}
