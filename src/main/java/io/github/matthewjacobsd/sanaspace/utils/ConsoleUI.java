package io.github.matthewjacobsd.sanaspace.utils;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Scanner;

@Component
public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in); // Scanner for user input
    private final LoggerUtil logger = new LoggerUtil(ConsoleUI.class); // Logger for tracking operations

    // Emoji constants (made public for ServiceRouter access)
    public static final String DOCTOR_EMOJI = "⚕️"; // Emoji for doctor menu
    public static final String PATIENT_EMOJI = "🩺"; // Emoji for patient menu
    public static final String MEDICATION_EMOJI = "💊"; // Emoji for medication menu
    public static final String INSURANCE_EMOJI = "🛡️"; // Emoji for insurance menu
    public static final String PRESCRIPTION_EMOJI = "📝"; // Emoji for prescription menu
    public static final String VISIT_EMOJI = "📅"; // Emoji for visit menu
    public static final String STATS_EMOJI = "📊"; // Emoji for analytics menu
    public static final String SUCCESS_EMOJI = "✅"; // Emoji for success messages
    public static final String ERROR_EMOJI = "❌"; // Emoji for error messages
    public static final String WARNING_EMOJI = "⚠️"; // Emoji for warning messages
    public static final String INFO_EMOJI = "ℹ️"; // Emoji for info messages
    public static final String DIVIDER = "━".repeat(40); // Divider line for UI formatting

    // Displays the main menu
    public void displayMainMenu() {
        long startTime = logger.startOperation("Displaying main menu", Map.of()); // Log start
        System.out.println("\n" + DIVIDER); // Print top divider
        System.out.println("🔥 Main Menu:"); // Print menu header
        System.out.println(DIVIDER); // Print divider
        System.out.println("1. " + DOCTOR_EMOJI + " Doctors"); // Doctor option
        System.out.println("2. " + PATIENT_EMOJI + " Patients"); // Patient option
        System.out.println("3. " + MEDICATION_EMOJI + " Medications"); // Medication option
        System.out.println("4. " + INSURANCE_EMOJI + " Insurances"); // Insurance option
        System.out.println("5. " + PRESCRIPTION_EMOJI + " Prescriptions"); // Prescription option
        System.out.println("6. " + VISIT_EMOJI + " Visits"); // Visit option
        System.out.println("7. " + STATS_EMOJI + " Analytics"); // Analytics option
        System.out.println("0. ♨️ Exit"); // Exit option
        System.out.print("🟢 Choose an option: "); // Prompt for input
        logger.success("Main menu displayed successfully", startTime); // Log success
    }

    // Displays entity-specific menu
    public void displayEntityMenu(String entityName, String emoji) {
        long startTime = logger.startOperation("Displaying " + entityName + " menu", Map.of("entity", entityName)); // Log start
        System.out.println("\n" + DIVIDER); // Print top divider
        System.out.println(emoji + " " + entityName + " Menu:"); // Print menu header
        System.out.println(DIVIDER); // Print divider
        System.out.println("1. Create new " + entityName); // Create option
        System.out.println("2. View all " + entityName + "s"); // View all option
        System.out.println("3. View single " + entityName); // View single option
        System.out.println("4. Update " + entityName); // Update option
        System.out.println("5. Delete " + entityName); // Delete option
        System.out.println("0. Back to main menu"); // Back option
        System.out.print("🟢 Choose an option: "); // Prompt for input
        logger.success(entityName + " menu displayed successfully", startTime); // Log success
    }

    // Gets and validates user menu choice
    public int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine()); // Parse input as integer
        } catch (NumberFormatException e) {
            showError("Invalid input! Please enter a number."); // Show error for non-numeric input
            logger.warn("Invalid menu choice input"); // Log warning
            return -1; // Return invalid choice
        }
    }

    // Displays success message with emoji
    public void showSuccess(String message) {
        System.out.println(SUCCESS_EMOJI + " " + message); // Print success message
        logger.info(message); // Log message
    }

    // Displays error message with emoji
    public void showError(String message) {
        System.out.println(ERROR_EMOJI + " " + message); // Print error message
        logger.warn(message); // Log warning
    }

    // Displays warning message with emoji
    public void showWarning(String message) {
        System.out.println(WARNING_EMOJI + " " + message); // Print warning message
        logger.warn(message); // Log warning
    }

    // Displays info message with emoji
    public void showInfo(String message) {
        System.out.println(INFO_EMOJI + " " + message); // Print info message
        logger.info(message); // Log message
    }

    // Displays welcome message
    public void displayWelcome() {
        long startTime = logger.startOperation("Displaying welcome message", Map.of()); // Log start
        System.out.println("\n" + DIVIDER); // Print top divider
        System.out.println("⚡ Sanaspace Hospital Database Console " + DOCTOR_EMOJI); // Print welcome message
        System.out.println(DIVIDER); // Print bottom divider
        logger.success("Welcome message displayed successfully", startTime); // Log success
    }

    // Displays exit message
    public void displayExit() {
        System.out.println("\n" + DIVIDER); // Print top divider
        System.out.println("👋 Exiting Sanaspace. Goodbye!"); // Print exit message
        System.out.println(DIVIDER); // Print bottom divider
        logger.info("Exiting Console UI"); // Log exit
    }
}