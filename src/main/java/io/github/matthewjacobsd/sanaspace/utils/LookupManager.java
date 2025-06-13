package io.github.matthewjacobsd.sanaspace.utils;

import io.github.matthewjacobsd.sanaspace.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class LookupManager {
    private final ConsoleUI consoleUI;
    private final Scanner scanner = new Scanner(System.in);
    private final LoggerUtil logger = new LoggerUtil(LookupManager.class);

    public <T> String lookupEntity(EntityLookup<T> lookup, String prompt) {
        long startTime = logger.startOperation("Performing lookup for " + lookup.getEntityName(), null);
        int page = 0;
        while (true) {
            Page<T> entityPage = lookup.fetchPage(PageRequest.of(page, 10));
            List<T> entities = entityPage.getContent();
            if (entities.isEmpty()) {
                consoleUI.showError("No " + lookup.getEntityName() + "s found!");
                logger.warn("No " + lookup.getEntityName() + "s found");
                return null;
            }

            System.out.println("\n" + ConsoleUI.DIVIDER);
            System.out.println(prompt + ":");
            System.out.println(ConsoleUI.DIVIDER);
            for (int i = 0; i < entities.size(); i++) {
                System.out.println((i + 1) + ". " + lookup.getDisplayName(entities.get(i)));
            }
            System.out.println("📄 Page " + (page + 1) + " of " + entityPage.getTotalPages());
            System.out.println("n. Next page | p. Previous page | 0. Cancel");
            System.out.print("🟢 Select " + lookup.getEntityName() + " (1-" + entities.size() + "): ");

            String input = scanner.nextLine();
            if (input.equals("0")) {
                consoleUI.showInfo("Selection cancelled");
                logger.info("Lookup cancelled for " + lookup.getEntityName());
                return null;
            }
            if (input.equals("n") && entityPage.hasNext()) {
                page++;
                continue;
            }
            if (input.equals("p") && entityPage.hasPrevious()) {
                page--;
                continue;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice < 1 || choice > entities.size()) {
                    consoleUI.showError("Invalid selection!");
                    logger.warn("Invalid lookup selection: " + choice);
                    continue;
                }
                T selected = entities.get(choice - 1);
                String id = getEntityId(selected);
                logger.success("Selected " + lookup.getEntityName() + " with ID: " + id, startTime);
                return id;
            } catch (NumberFormatException e) {
                consoleUI.showError("Invalid input! Enter a number.");
                logger.warn("Invalid lookup input: " + input);
            }
        }
    }

    private <T> String getEntityId(T entity) {
        if (entity instanceof Doctor d) return d.getId();
        if (entity instanceof Patient p) return p.getId();
        if (entity instanceof Medication m) return m.getId();
        if (entity instanceof Insurance i) return i.getId();
        if (entity instanceof Prescription p) return p.getId();
        throw new IllegalArgumentException("Unknown entity type");
    }
}