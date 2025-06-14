package io.github.matthewjacobsd.sanaspace.utils;

import io.github.matthewjacobsd.sanaspace.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component // Marks this class as a Spring component
@RequiredArgsConstructor // Generates constructor for final fields
public class LookupManager {
    private final ConsoleUI consoleUI; // ConsoleUI dependency for UI operations
    private final Scanner scanner = new Scanner(System.in); // Scanner for user input
    private final LoggerUtil logger = new LoggerUtil(LookupManager.class); // Logger for tracking operations

    public <T> String lookupEntity(EntityLookup<T> lookup, String prompt) { // Looks up an entity by paginated selection
        long startTime = logger.startOperation("Performing lookup for " + lookup.getEntityName(), null); // Logs start of lookup
        int page = 0; // Tracks current page
        while (true) { // Pagination loop
            Page<T> entityPage = lookup.fetchPage(PageRequest.of(page, 10)); // Fetches page of entities
            List<T> entities = entityPage.getContent(); // Gets entities from page
            if (entities.isEmpty()) { // Checks if no entities found
                consoleUI.showError("No " + lookup.getEntityName() + "s found!"); // Shows error
                logger.warn("No " + lookup.getEntityName() + "s found"); // Logs warning
                return null; // Returns null if empty
            }

            System.out.println("\n" + ConsoleUI.DIVIDER); // Prints divider
            System.out.println(prompt + ":"); // Prints prompt
            System.out.println(ConsoleUI.DIVIDER); // Prints divider
            for (int i = 0; i < entities.size(); i++) { // Loops through entities
                System.out.println((i + 1) + ". " + lookup.getDisplayName(entities.get(i))); // Prints entity option
            }
            System.out.println("Page " + (page + 1) + " of " + entityPage.getTotalPages()); // Shows page info
            System.out.println("n. Next page | p. Previous page | 0. Cancel"); // Shows navigation options
            System.out.print("Select " + lookup.getEntityName() + " (1-" + entities.size() + "): "); // Prompts for selection

            String input = scanner.nextLine(); // Gets user input
            if (input.equals("0")) { // Checks for cancel
                consoleUI.showInfo("Selection cancelled"); // Shows cancellation message
                logger.info("Lookup cancelled for " + lookup.getEntityName()); // Logs cancellation
                return null; // Returns null
            }
            if (input.equals("n") && entityPage.hasNext()) { // Checks for next page
                page++; // Increments page
                continue; // Continues loop
            }
            if (input.equals("p") && entityPage.hasPrevious()) { // Checks for previous page
                page--; // Decrements page
                continue; // Continues loop
            }

            try {
                int choice = Integer.parseInt(input); // Parses input to integer
                if (choice < 1 || choice > entities.size()) { // Validates choice
                    consoleUI.showError("Invalid selection!"); // Shows error
                    logger.warn("Invalid lookup selection: " + choice); // Logs warning
                    continue; // Continues loop
                }
                T selected = entities.get(choice - 1); // Gets selected entity
                String id = getEntityId(selected); // Gets entity ID
                logger.success("Selected " + lookup.getEntityName() + " with ID: " + id, startTime); // Logs success
                return id; // Returns entity ID
            } catch (NumberFormatException e) { // Handles invalid input
                consoleUI.showError("Invalid input! Enter a number."); // Shows error
                logger.warn("Invalid lookup input: " + input); // Logs warning
            }
        }
    }

    private <T> String getEntityId(T entity) { // Extracts ID from entity
        if (entity instanceof Doctor d) return d.getId(); // Returns Doctor ID
        if (entity instanceof Patient p) return p.getId(); // Returns Patient ID
        if (entity instanceof Medication m) return m.getId(); // Returns Medication ID
        if (entity instanceof Insurance i) return i.getId(); // Returns Insurance ID
        if (entity instanceof Prescription p) return p.getId(); // Returns Prescription ID
        throw new IllegalArgumentException("Unknown entity type"); // Throws error for unknown type
    }
}