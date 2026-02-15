# Jeff's Car Rental

Interactive CLI application for managing vehicle rental summaries. Add rentals, print customer receipts and generate company daily summaries - all from the terminal.

## Features

- **Add rentals** — step-by-step input for vehicle type, kilometers, energy consumption, extras
- **Customer receipt** — detailed ASCII receipt for a single rental with itemized charges
- **Company daily summary** — overview of all rentals with subtotals and grand total (company revenue)
- **Input validation** — re-prompts on invalid input without crashing

## Screenshots

### Main Menu

Simple menu with options to add rentals, print customer receipts and generate company daily summaries.

![Main Menu](src/main/resources/screenshots/1.png)

### Customer Receipt
Add Rental Vehicle Summary:

![Add Rental Vehicle Summary](src/main/resources/screenshots/2.png)

Print Customer Receipt:

![Print Customer Receipt](src/main/resources/screenshots/3.png)

### Company Daily Summary
Print Test Company Daily Summary:
![Company Daily Summary](src/main/resources/screenshots/4.png)

## Vehicle Types

| Type | Rate/km | Energy Rate | Unit |
|------|---------|-------------|------|
| Compact Van | 0.82 CHF | 1.95 CHF | L |
| Large Van | 1.05 CHF | 1.95 CHF | L |
| E-Van | 0.68 CHF | 0.30 CHF | kWh |

## How to Run

### Prerequisites

- Java 21+
- Maven 3.8+

### Run the application
- Start the main() method in the RentalSummaryApp.class and follow printed commands
- Or run the application directly from the command line:

```bash
mvn compile exec:java -Dexec.mainClass=com.swissre.rental.RentalSummaryApp
```

Or after building:

```bash
mvn package -DskipTests
java -cp target/classes com.swissre.rental.RentalSummaryApp
```

### Run the tests

```bash
mvn clean test
```

Have fun!
