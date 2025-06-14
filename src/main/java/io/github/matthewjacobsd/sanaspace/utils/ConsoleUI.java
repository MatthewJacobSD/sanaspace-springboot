package io.github.matthewjacobsd.sanaspace.utils;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Scanner;

@Component // Marks this class as a Spring component for dependency injection
public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in); // Initializes Scanner for user input
    private final LoggerUtil logger = new LoggerUtil(ConsoleUI.class); // Logger for tracking operations

    // Text constants instead of emojis
    public static final String SUCCESS_EMOJI = "[OK]"; // Success message prefix
    public static final String ERROR_EMOJI = "[ERR]"; // Error message prefix
    public static final String WARNING_EMOJI = "[WARN]"; // Warning message prefix
    public static final String INFO_EMOJI = "[INFO]"; // Info message prefix
    public static final String DIVIDER = "=".repeat(40); // Divider line for UI formatting

    public void displayMainMenu() { // Displays the main menu
        long startTime = logger.startOperation("Displaying main menu", Map.of()); // Logs start of menu display
        System.out.println("\n" + DIVIDER); // Prints divider
        System.out.println("Main Menu:"); // Prints menu title
        System.out.println(DIVIDER); // Prints divider
        System.out.println("1. " + " Doctors"); // Option for doctors menu
        System.out.println("2. " + " Patients"); // Option for patients menu
        System.out.println("3. " + " Medications"); // Option for medications menu
        System.out.println("4. " + " Insurances"); // Option for insurances menu
        System.out.println("5. " + " Prescriptions"); // Option for prescriptions menu
        System.out.println("6. " + " Visits"); // Option for visits menu
        System.out.println("7. " + " Analytics"); // Option for analytics menu
        System.out.println("0. Exit"); // Option to exit
        System.out.print("Choose an option: "); // Prompts user for input
        logger.success("Main menu displayed successfully", startTime); // Logs successful menu display
    }

    public void displayEntityMenu(String entityName, String prefix, String[] options) { // Displays custom entity menu with options
        System.out.println("\n" + prefix + " Menu:"); // Prints menu title with prefix
        for (int i = 0; i < options.length; i++) { // Loops through options
            System.out.println((i + 1) + ". " + options[i] + " " + entityName); // Prints each option
        }
        System.out.println("0. Back to Main Menu"); // Option to return to main menu
        System.out.print("Choose an option: "); // Prompts user for input
    }

    public void displayEntityMenu(String entityName, String emoji) { // Displays standard entity menu
        long startTime = logger.startOperation("Displaying " + entityName + " menu", Map.of("entity", entityName)); // Logs start of entity menu display
        System.out.println("\n" + DIVIDER); // Prints divider
        System.out.println(emoji + " " + entityName + " Menu:"); // Prints menu title with emoji
        System.out.println(DIVIDER); // Prints divider
        System.out.println("1. Create new " + entityName); // Option to create entity
        System.out.println("2. View all " + entityName + "s"); // Option to view all entities
        System.out.println("3. View single " + entityName); // Option to view single entity
        System.out.println("4. Update " + entityName); // Option to update entity
        System.out.println("5. Delete " + entityName); // Option to delete entity
        System.out.println("0. Back to main menu"); // Option to return to main menu
        System.out.print("Choose an option: "); // Prompts user for input
        logger.success(entityName + " menu displayed successfully", startTime); // Logs successful menu display
    }

    public int getUserChoice() { // Gets user's menu choice
        try {
            return Integer.parseInt(scanner.nextLine()); // Parses user input to integer
        } catch (NumberFormatException e) { // Handles invalid input
            showError("Invalid input! Please enter a number."); // Shows error message
            logger.warn("Invalid menu choice input"); // Logs warning
            return -1; // Returns -1 for invalid input
        }
    }

    public void showSuccess(String message) { // Displays success message
        System.out.println(SUCCESS_EMOJI + " " + message); // Prints success message with emoji
        logger.info(message); // Logs info message
    }

    public void showError(String message) { // Displays error message
        System.out.println(ERROR_EMOJI + " " + message); // Prints error message with emoji
        logger.warn(message); // Logs warning
    }

    public void showWarning(String message) { // Displays warning message
        System.out.println(WARNING_EMOJI + " " + message); // Prints warning message with emoji
        logger.warn(message); // Logs warning
    }

    public void showInfo(String message) { // Displays info message
        System.out.println(INFO_EMOJI + " " + message); // Prints info message with emoji
        logger.info(message); // Logs info message
    }

    public void displayWelcome() { // Displays welcome message
        long startTime = logger.startOperation("Displaying welcome message", Map.of()); // Logs start of welcome display
        System.out.println("\n" + DIVIDER); // Prints divider
        System.out.println("Sanaspace Hospital Database Console"); // Prints welcome message
        System.out.println(DIVIDER); // Prints divider
        logger.success("Welcome message displayed successfully", startTime); // Logs successful welcome display
    }

    public void displayExit() { // Displays exit message
        System.out.println("\n" + DIVIDER); // Prints divider
        System.out.println("Exiting Sanaspace. Goodbye!"); // Prints exit message
        System.out.println(DIVIDER); // Prints divider
        logger.info("Exiting Console UI"); // Logs exit
    }
}