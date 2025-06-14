package io.github.matthewjacobsd.sanaspace.utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import io.github.matthewjacobsd.sanaspace.controllers.record.CAnalytics;
import io.github.matthewjacobsd.sanaspace.models.Doctor;
import io.github.matthewjacobsd.sanaspace.models.Insurance;
import io.github.matthewjacobsd.sanaspace.models.Medication;
import io.github.matthewjacobsd.sanaspace.models.Patient;
import io.github.matthewjacobsd.sanaspace.models.Prescription;
import io.github.matthewjacobsd.sanaspace.models.Visit;
import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;
import io.github.matthewjacobsd.sanaspace.services.SDoctor;
import io.github.matthewjacobsd.sanaspace.services.SInsurance;
import io.github.matthewjacobsd.sanaspace.services.SMedication;
import io.github.matthewjacobsd.sanaspace.services.SPatient;
import io.github.matthewjacobsd.sanaspace.services.SPrescription;
import io.github.matthewjacobsd.sanaspace.services.SVisit;

import lombok.RequiredArgsConstructor;

@Component // Marks class as a Spring component
@RequiredArgsConstructor // Generates constructor for final fields
public class ServiceRouter implements CommandLineRunner {

    // Service dependencies for various entities
    private final SDoctor doctorService; // Doctor service
    private final SPatient patientService; // Patient service
    private final SMedication medicationService; // Medication service
    private final SInsurance insuranceService; // Insurance service
    private final SPrescription prescriptionService; // Prescription service
    private final SVisit visitService; // Visit service
    private final CAnalytics analyticsService; // Analytics service
    private final ConsoleUI consoleUI; // Console UI for user interaction
    private final LookupManager lookupManager; // Lookup manager for entity selection
    private final Scanner scanner = new Scanner(System.in); // Scanner for user input
    private final LoggerUtil logger = new LoggerUtil(ServiceRouter.class); // Logger for tracking operations

    @Override
    public void run(String... args) throws Exception { // Runs application loop on startup
        long startTime = logger.startOperation("Starting application loop", Map.of()); // Logs start
        consoleUI.displayWelcome(); // Displays welcome message
        displayMainMenuLoop(); // Starts main menu loop
        consoleUI.displayExit(); // Displays exit message
        logger.success("Application loop completed successfully", startTime); // Logs successful completion
    }

    private void displayMainMenuLoop() { // Main menu loop
        while (true) { // Continues until user exits
            consoleUI.displayMainMenu(); // Displays main menu
            int choice = consoleUI.getUserChoice(); // Gets user choice
            if (choice == -1) continue; // Skips invalid input
            if (choice == 0) return; // Exits loop on choice 0
            route(choice); // Routes to appropriate menu
        }
    }

    public void route(int choice) { // Routes main menu choice
        long startTime = logger.startOperation("Routing menu choice", Map.of("choice", choice)); // Logs start
        try {
            switch (choice) { // Handles menu choice
                case 1 -> displayEntityMenuLoop("Doctor", "[Doctor]"); // Doctor menu
                case 2 -> displayEntityMenuLoop("Patient", "[Patient]"); // Patient menu
                case 3 -> displayEntityMenuLoop("Medication", "[Medication]"); // Medication menu
                case 4 -> displayEntityMenuLoop("Insurance", "[Insurance]"); // Insurance menu
                case 5 -> displayEntityMenuLoop("Prescription", "[Prescription]"); // Prescription menu
                case 6 -> displayEntityMenuLoop("Visit", "[Visit]"); // Visit menu
                case 7 -> showAnalytics(); // Analytics menu
                default -> { // Invalid choice
                    consoleUI.showError("Invalid choice!"); // Shows error
                    logger.warn("Invalid menu choice: " + choice); // Logs warning
                }
            }
            logger.success("Menu choice routed successfully", startTime); // Logs success
        } catch (Exception e) { // Handles errors
            logger.error("Failed to route menu choice", startTime, e, "Choice: " + choice); // Logs error
            consoleUI.showError("Error processing choice: " + e.getMessage()); // Shows error
        }
    }

    private void displayEntityMenuLoop(String entityName, String prefix) { // Entity menu loop
        while (true) { // Continues until user returns
            if (entityName.equals("Patient")) { // Special case for Patient menu
                consoleUI.displayEntityMenu(entityName, prefix, new String[]{"Create", "View All", "View Single", "Update", "Delete", "View Main Doctor"}); // Custom patient menu
            } else {
                consoleUI.displayEntityMenu(entityName, prefix); // Standard entity menu
            }
            int choice = consoleUI.getUserChoice(); // Gets user choice
            if (choice == -1) continue; // Skips invalid input
            if (choice == 0) return; // Returns to main menu
            handleEntityOperation(entityName.toLowerCase(), choice); // Handles entity operation
        }
    }

    public void handleEntityOperation(String entity, int operation) { // Routes entity operation
        long startTime = logger.startOperation("Handling " + entity + " operation", Map.of("entity", entity, "operation", operation)); // Logs start
        try {
            switch (entity) { // Routes to entity-specific operation
                case "doctor" -> handleDoctorOperation(operation); // Doctor operations
                case "patient" -> handlePatientOperation(operation); // Patient operations
                case "medication" -> handleMedicationOperation(operation); // Medication operations
                case "insurance" -> handleInsuranceOperation(operation); // Insurance operations
                case "prescription" -> handlePrescriptionOperation(operation); // Prescription operations
                case "visit" -> handleVisitOperation(operation); // Visit operations
                default -> { // Invalid entity
                    consoleUI.showError("Invalid entity!"); // Shows error
                    logger.warn("Invalid entity: " + entity); // Logs warning
                }
            }
            logger.success(entity + " operation completed successfully", startTime); // Logs success
        } catch (Exception e) { // Handles errors
            logger.error("Failed to handle " + entity + " operation", startTime, e, "Operation: " + operation); // Logs error
            consoleUI.showError("Error: " + e.getMessage()); // Shows error
        }
    }

    private Map<String, Boolean> selectFields(String entityName) { // Selects fields to display for entity
        Map<String, Boolean> fields = new HashMap<>(); // Initializes field map
        consoleUI.showInfo("Select fields to display for " + entityName + " (y/n for each, Enter for default):"); // Prompts user

        switch (entityName.toLowerCase()) { // Sets default fields based on entity
            case "doctor" -> { // Doctor fields
                fields.put("firstName", true); // Default: show first name
                fields.put("lastName", true); // Default: show last name
                fields.put("email", false); // Default: hide email
                fields.put("address", false); // Default: hide address
                fields.put("specialization", false); // Default: hide specialization
                fields.put("experience", false); // Default: hide experience
            }
            case "patient" -> { // Patient fields
                fields.put("firstName", true); // Default: show first name
                fields.put("lastName", true); // Default: show last name
                fields.put("email", false); // Default: hide email
                fields.put("postcode", false); // Default: hide postcode
                fields.put("address", false); // Default: hide address
                fields.put("phoneNumber", false); // Default: hide phone number
            }
            case "medication" -> { // Medication fields
                fields.put("name", true); // Default: show name
                fields.put("sideEffects", false); // Default: hide side effects
                fields.put("benefits", false); // Default: hide benefits
            }
            case "insurance" -> { // Insurance fields
                fields.put("companyName", true); // Default: show company name
                fields.put("address", false); // Default: hide address
                fields.put("phoneNumber", false); // Default: hide phone number
            }
            case "prescription" -> { // Prescription fields
                fields.put("id", true); // Default: show ID
                fields.put("prescriptionDate", false); // Default: hide date
                fields.put("dosage", false); // Default: hide dosage
                fields.put("duration", false); // Default: hide duration
                fields.put("comments", false); // Default: hide comments
            }
            case "visit" -> { // Visit fields
                fields.put("visitDate", true); // Default: show visit date
                fields.put("patientId", true); // Default: show patient ID
                fields.put("doctorId", false); // Default: hide doctor ID
                fields.put("symptoms", false); // Default: hide symptoms
                fields.put("diagnosis", false); // Default: hide diagnosis
            }
            default -> { // Unknown entity
                logger.warn("Unknown entity: " + entityName); // Logs warning
                return fields; // Returns empty map
            }
        }

        fields.forEach((field, enabled) -> { // Prompts user for each field
            consoleUI.showInfo("Show " + field + "? (y/n, Enter for " + (enabled ? "yes" : "no") + "): "); // Shows prompt
            String input = scanner.nextLine().trim().toLowerCase(); // Gets user input
            boolean newValue = enabled; // Uses default value
            if (input.equals("y")) newValue = true; // Sets to true if 'y'
            else if (input.equals("n")) newValue = false; // Sets to false if 'n'
            fields.put(field, newValue); // Updates field value
            logger.info("Field " + field + " set to " + newValue + " for " + entityName); // Logs field selection
        });

        if (fields.values().stream().noneMatch(Boolean::booleanValue)) { // Ensures at least one field is selected
            logger.warn("No fields selected for " + entityName + ", enabling default field"); // Logs warning
            fields.put(fields.keySet().iterator().next(), true); // Enables first field
        }
        logger.info("Final selected fields for " + entityName + ": " + fields); // Logs final fields
        return fields; // Returns selected fields
    }

    private <T> String formatItem(T item, String entityName, Map<String, Boolean> fields) { // Formats entity for display
        if (item == null) { // Checks for null item
            logger.warn("Null item received for " + entityName); // Logs warning
            return "[" + entityName + "]: (null)"; // Returns null display
        }
        StringBuilder display = new StringBuilder("[" + entityName + "]: "); // Initializes display string
        logger.info("Formatting item for " + entityName + ": " + item); // Logs formatting start

        switch (entityName.toLowerCase()) { // Formats based on entity type
            case "doctor" -> { // Doctor formatting
                Doctor doctor = (Doctor) item; // Casts to Doctor
                if (fields.getOrDefault("firstName", false)) display.append(doctor.getFirstName() != null ? doctor.getFirstName() : "(null)").append(" "); // Adds first name
                if (fields.getOrDefault("lastName", false)) display.append(doctor.getLastName() != null ? doctor.getLastName() : "(null)").append(" "); // Adds last name
                if (fields.getOrDefault("email", false)) display.append(", Email: ").append(doctor.getEmail() != null ? doctor.getEmail() : "(null)"); // Adds email
                if (fields.getOrDefault("address", false)) display.append(", Address: ").append(doctor.getAddress() != null ? doctor.getAddress() : "(null)"); // Adds address
                if (fields.getOrDefault("specialization", false)) display.append(", Specialization: ").append(doctor.getSpecialization() != null ? doctor.getSpecialization() : "(null)"); // Adds specialization
                if (fields.getOrDefault("experience", false)) display.append(", Experience: ").append(doctor.getExperience() != null ? doctor.getExperience() : "(null)"); // Adds experience
            }
            case "patient" -> { // Patient formatting
                Patient patient = (Patient) item; // Casts to Patient
                if (fields.getOrDefault("firstName", false)) display.append(patient.getFirstName() != null ? patient.getFirstName() : "(null)").append(" "); // Adds first name
                if (fields.getOrDefault("lastName", false)) display.append(patient.getLastName() != null ? patient.getLastName() : "(null)").append(" "); // Adds last name
                if (fields.getOrDefault("email", false)) display.append(", Email: ").append(patient.getEmail() != null ? patient.getEmail() : "(null)"); // Adds email
                if (fields.getOrDefault("postcode", false)) display.append(", Postcode: ").append(patient.getPostcode() != null ? patient.getPostcode() : "(null)"); // Adds postcode
                if (fields.getOrDefault("address", false)) display.append(", Address: ").append(patient.getAddress() != null ? patient.getAddress() : "(null)"); // Adds address
                if (fields.getOrDefault("phoneNumber", false)) display.append(", Phone: ").append(patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "(null)"); // Adds phone number
            }
            case "medication" -> { // Medication formatting
                Medication medication = (Medication) item; // Casts to Medication
                if (fields.getOrDefault("name", false)) display.append(medication.getName() != null ? medication.getName() : "(null)"); // Adds name
                if (fields.getOrDefault("sideEffects", false)) display.append(", Side Effects: ").append(medication.getSideEffects() != null ? medication.getSideEffects() : "(null)"); // Adds side effects
                if (fields.getOrDefault("benefits", false)) display.append(", Benefits: ").append(medication.getBenefits() != null ? medication.getBenefits() : "(null)"); // Adds benefits
            }
            case "insurance" -> { // Insurance formatting
                Insurance insurance = (Insurance) item; // Casts to Insurance
                if (fields.getOrDefault("companyName", false)) display.append(insurance.getCompanyName() != null ? insurance.getCompanyName() : "(null)"); // Adds company name
                if (fields.getOrDefault("address", false)) display.append(", Address: ").append(insurance.getAddress() != null ? insurance.getAddress() : "(null)"); // Adds address
                if (fields.getOrDefault("phoneNumber", false)) display.append(", Phone: ").append(insurance.getPhoneNumber() != null ? insurance.getPhoneNumber() : "(null)"); // Adds phone number
            }
            case "prescription" -> { // Prescription formatting
                Prescription prescription = (Prescription) item; // Casts to Prescription
                if (fields.getOrDefault("id", false)) display.append(prescription.getId() != null ? prescription.getId() : "(null)"); // Adds ID
                if (fields.getOrDefault("prescriptionDate", false)) display.append(", Date: ").append(prescription.getPrescriptionDate() != null ? prescription.getPrescriptionDate() : "(null)"); // Adds date
                if (fields.getOrDefault("dosage", false)) display.append(", Dosage: ").append(prescription.getDosage() != 0 ? prescription.getDosage() : "(null)"); // Adds dosage
                if (fields.getOrDefault("duration", false)) display.append(", Duration: ").append(prescription.getDuration() != 0 ? prescription.getDuration() : "(null)"); // Adds duration
                if (fields.getOrDefault("comments", false)) display.append(", Comments: ").append(prescription.getComments() != null ? prescription.getComments() : "(null)"); // Adds comments
            }
            case "visit" -> { // Visit formatting
                Visit visit = (Visit) item; // Casts to Visit
                if (fields.getOrDefault("visitDate", false)) display.append(visit.getId().getVisitDate() != null ? visit.getId().getVisitDate() : "(null)").append(" "); // Adds visit date
                if (fields.getOrDefault("patientId", false)) display.append(", Patient: ").append(visit.getId().getPatientId() != null ? visit.getId().getPatientId() : "(null)"); // Adds patient ID
                if (fields.getOrDefault("doctorId", false)) display.append(", Doctor: ").append(visit.getId().getDoctorId() != null ? visit.getId().getDoctorId() : "(null)"); // Adds doctor ID
                if (fields.getOrDefault("symptoms", false)) display.append(", Symptoms: ").append(visit.getSymptoms() != null ? visit.getSymptoms() : "(null)"); // Adds symptoms
                if (fields.getOrDefault("diagnosis", false)) display.append(", Diagnosis: ").append(visit.getDiagnosis() != 0 ? visit.getDiagnosis() : "(null)"); // Adds diagnosis
            }
            default -> { // Unknown entity
                logger.warn("Unknown entity for formatting: " + entityName); // Logs warning
                return "[" + entityName + "]: (unknown)"; // Returns unknown display
            }
        }
        String result = display.toString().trim(); // Trims final string
        logger.info("Formatted " + entityName + ": " + result); // Logs formatted result
        return result; // Returns formatted string
    }

    private <T> void handlePagination(String entityName, Function<PageRequest, Page<T>> fetchPage) { // Handles paginated entity display
        int pageSize = 10; // Default page size
        consoleUI.showInfo("Enter items per page (e.g., 5, 10, 20) or press Enter for default (10):"); // Prompts for page size
        String input = scanner.nextLine().trim(); // Gets user input
        try {
            if (!input.isEmpty()) { // Checks if input provided
                pageSize = Integer.parseInt(input); // Parses page size
                if (pageSize <= 0) { // Validates page size
                    consoleUI.showError("Page size must be positive!"); // Shows error
                    pageSize = 10; // Reverts to default
                }
            }
        } catch (NumberFormatException e) { // Handles invalid input
            consoleUI.showError("Invalid number! Using default page size (10)."); // Shows error
        }

        Map<String, Boolean> fields = selectFields(entityName); // Gets selected fields

        int currentPage = 0; // Tracks current page
        while (true) { // Pagination loop
            Page<T> page = fetchPage.apply(PageRequest.of(currentPage, pageSize)); // Fetches page
            logger.info("Fetched page " + (currentPage + 1) + " for " + entityName + ": " + page.getContent().size() + " items, total: " + page.getTotalElements()); // Logs page fetch
            PaginationResponse<T> response = new PaginationResponse<>(page); // Wraps page in response

            consoleUI.showInfo(entityName + " List (Page " + (response.getPage() + 1) + " of "
                    + response.getTotalPages() + ", Total: " + response.getTotalItems() + ")"); // Shows page info
            if (response.getItems().isEmpty()) { // Checks if page is empty
                consoleUI.showWarning("No " + entityName.toLowerCase() + "s found."); // Shows warning
            } else {
                response.getItems().forEach(item -> System.out.println(formatItem(item, entityName, fields))); // Displays items
            }

            consoleUI.showInfo("Enter 'n' for next page, 'p' for previous page, or 'q' to quit:"); // Prompts for navigation
            String nav = scanner.nextLine().trim().toLowerCase(); // Gets navigation input
            if (nav.equals("q")) break; // Exits on 'q'
            else if (nav.equals("n") && currentPage < page.getTotalPages() - 1) currentPage++; // Next page
            else if (nav.equals("p") && currentPage > 0) currentPage--; // Previous page
            else if (!nav.isEmpty()) consoleUI.showError("Invalid input! Use 'n', 'p', or 'q'."); // Invalid input
        }
    }

    private <E extends Enum<E>> E selectEnum(Class<E> enumClass, String fieldName) { // Selects enum value
        E[] values = enumClass.getEnumConstants(); // Gets enum values
        consoleUI.showInfo("Available " + fieldName + " options:"); // Shows prompt
        for (int i = 0; i < values.length; i++) { // Lists options
            System.out.println((i + 1) + ". " + values[i].name()); // Prints enum option
        }
        System.out.print("Choose an option (1-" + values.length + "): "); // Prompts for choice
        while (true) { // Loops until valid choice
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim()); // Parses input
                if (choice >= 1 && choice <= values.length) { // Validates choice
                    return values[choice - 1]; // Returns selected enum
                } else {
                    consoleUI.showError("Invalid choice! Pick a number between 1 and " + values.length); // Shows error
                }
            } catch (NumberFormatException e) { // Handles invalid input
                consoleUI.showError("Invalid input! Enter a number."); // Shows error
            }
        }
    }

    private void handleDoctorOperation(int operation) { // Handles doctor operations
        switch (operation) { // Routes operation
            case 1 -> { // Create doctor
                Doctor doctor = new Doctor(); // Creates new doctor
                System.out.print("Enter first name: "); // Prompts for first name
                doctor.setFirstName(scanner.nextLine().trim()); // Sets first name
                System.out.print("Enter last name: "); // Prompts for last name
                doctor.setLastName(scanner.nextLine().trim()); // Sets last name
                System.out.print("Enter address: "); // Prompts for address
                doctor.setAddress(scanner.nextLine().trim()); // Sets address
                System.out.print("Enter email: "); // Prompts for email
                doctor.setEmail(scanner.nextLine().trim()); // Sets email
                doctor.setSpecialization(selectEnum(Doctor.Specialization.class, "Specialization")); // Sets specialization
                doctor.setExperience(selectEnum(Doctor.Experience.class, "Experience")); // Sets experience
                doctorService.saveDoctor(doctor); // Saves doctor
                consoleUI.showSuccess("Doctor created successfully"); // Shows success
            }
            case 2 -> handlePagination("Doctors", pageRequest -> doctorService.getPagedDoctors(pageRequest)); // View all doctors
            case 3 -> { // View single doctor
                String id = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor"); // Selects doctor
                if (id == null) return; // Exits if no selection
                Doctor doctor = doctorService.fetchDoctorById(id); // Fetches doctor
                Map<String, Boolean> fields = selectFields("Doctor"); // Gets display fields
                System.out.println(formatItem(doctor, "Doctor", fields)); // Displays doctor
            }
            case 4 -> { // Update doctor
                String id = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor to Update"); // Selects doctor
                if (id == null) return; // Exits if no selection
                Map<String, Object> updates = new HashMap<>(); // Initializes updates map
                System.out.print("Enter field to update (firstName/lastName/address/email/specialization/experience): "); // Prompts for field
                String field = scanner.nextLine().trim(); // Gets field
                switch (field) { // Handles field update
                    case "specialization" -> updates.put(field, selectEnum(Doctor.Specialization.class, "Specialization").name()); // Updates specialization
                    case "experience" -> updates.put(field, selectEnum(Doctor.Experience.class, "Experience").name()); // Updates experience
                    default -> { // Other fields
                        System.out.print("Enter new value: "); // Prompts for value
                        updates.put(field, scanner.nextLine().trim()); // Adds update
                    }
                }
                doctorService.updateDoctorFields(id, updates); // Updates doctor
                consoleUI.showSuccess("Doctor updated successfully"); // Shows success
            }
            case 5 -> { // Delete doctor
                String id = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor to Delete"); // Selects doctor
                if (id == null) return; // Exits if no selection
                doctorService.deleteDoctor(id); // Deletes doctor
                consoleUI.showSuccess("Doctor deleted successfully"); // Shows success
            }
            default -> { // Invalid operation
                consoleUI.showError("Invalid operation for Doctor!"); // Shows error
                logger.warn("Invalid doctor operation: " + operation); // Logs warning
            }
        }
    }

    private void handlePatientOperation(int operation) { // Handles patient operations
        switch (operation) { // Routes operation
            case 1 -> { // Create patient
                Patient patient = new Patient(); // Creates new patient
                System.out.print("Enter first name: "); // Prompts for first name
                patient.setFirstName(scanner.nextLine().trim()); // Sets first name
                System.out.print("Enter last name: "); // Prompts for last name
                patient.setLastName(scanner.nextLine().trim()); // Sets last name
                System.out.print("Enter postcode: "); // Prompts for postcode
                patient.setPostcode(scanner.nextLine().trim()); // Sets postcode
                System.out.print("Enter address: "); // Prompts for address
                patient.setAddress(scanner.nextLine().trim()); // Sets address
                System.out.print("Enter phone number: "); // Prompts for phone
                patient.setPhoneNumber(PhoneUtils.formatPhoneNumber(scanner.nextLine().trim())); // Sets formatted phone
                System.out.print("Enter email: "); // Prompts for email
                patient.setEmail(scanner.nextLine().trim()); // Sets email
                String insuranceId = lookupManager.lookupEntity(EntityLookup.insuranceLookup(insuranceService), "Select Insurance (or 0 to skip)"); // Selects insurance
                if (insuranceId != null) { // Checks if insurance selected
                    patient.setInsurance(insuranceService.fetchInsuranceById(insuranceId)); // Sets insurance
                }
                patientService.savePatient(patient); // Saves patient
                consoleUI.showSuccess("Patient created successfully"); // Shows success
            }
            case 2 -> handlePagination("Patients", pageRequest -> patientService.getPagedPatients(pageRequest)); // View all patients
            case 3 -> { // View single patient
                String id = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient"); // Selects patient
                if (id == null) return; // Exits if no selection
                Patient patient = patientService.fetchPatientById(id); // Fetches patient
                Map<String, Boolean> fields = selectFields("Patient"); // Gets display fields
                System.out.println(formatItem(patient, "Patient", fields)); // Displays patient
            }
            case 4 -> { // Update patient
                String id = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient to Update"); // Selects patient
                if (id == null) return; // Exits if no selection
                Map<String, Object> updates = new HashMap<>(); // Initializes updates map
                System.out.print("Enter field to update (firstName/lastName/postcode/address/phoneNumber/email): "); // Prompts for field
                String field = scanner.nextLine().trim(); // Gets field
                System.out.print("Enter new value: "); // Prompts for value
                String value = scanner.nextLine().trim(); // Gets value
                updates.put(field, "phoneNumber".equals(field) ? PhoneUtils.formatPhoneNumber(value) : value); // Adds update
                patientService.updatePatientFields(id, updates); // Updates patient
                consoleUI.showSuccess("Patient updated successfully"); // Shows success
            }
            case 5 -> { // Delete patient
                String id = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient to Delete"); // Selects patient
                if (id == null) return; // Exits if no selection
                patientService.deletePatient(id); // Deletes patient
                consoleUI.showSuccess("Patient deleted successfully"); // Shows success
            }
            case 6 -> { // View main doctor
                String id = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient"); // Selects patient
                if (id == null) return; // Exits if no selection
                Page<Doctor> mainDoctors = visitService.findMainDoctorForPatient(id); // Fetches main doctor
                if (mainDoctors.isEmpty()) { // Checks if no visits found
                    consoleUI.showWarning("No visits found for patient " + id); // Shows warning
                } else {
                    Doctor mainDoctor = mainDoctors.getContent().get(0); // Gets main doctor
                    Map<String, Boolean> fields = selectFields("Doctor"); // Gets display fields
                    System.out.println("Main Doctor: " + formatItem(mainDoctor, "Doctor", fields)); // Displays main doctor
                    consoleUI.showSuccess("Main doctor retrieved successfully"); // Shows success
                }
            }
            default -> { // Invalid operation
                consoleUI.showError("Invalid operation for Patient!"); // Shows error
                logger.warn("Invalid patient operation: " + operation); // Logs warning
            }
        }
    }

    private void handleMedicationOperation(int operation) { // Handles medication operations
        switch (operation) { // Routes operation
            case 1 -> { // Create medication
                Medication medication = new Medication(); // Creates new medication
                System.out.print("Enter medication name: "); // Prompts for name
                medication.setName(scanner.nextLine().trim()); // Sets name
                System.out.print("Enter side effects: "); // Prompts for side effects
                medication.setSideEffects(scanner.nextLine().trim()); // Sets side effects
                System.out.print("Enter benefits: "); // Prompts for benefits
                medication.setBenefits(scanner.nextLine().trim()); // Sets benefits
                medicationService.saveMedication(medication); // Saves medication
                consoleUI.showSuccess("Medication created successfully"); // Shows success
            }
            case 2 -> handlePagination("Medications", pageRequest -> medicationService.getPagedMedications(pageRequest)); // View all medications
            case 3 -> { // View single medication
                String id = lookupManager.lookupEntity(EntityLookup.medicationLookup(medicationService), "Select Medication"); // Selects medication
                if (id == null) return; // Exits if no selection
                Medication medication = medicationService.fetchMedicationById(id); // Fetches medication
                Map<String, Boolean> fields = selectFields("Medication"); // Gets display fields
                System.out.println(formatItem(medication, "Medication", fields)); // Displays medication
            }
            case 4 -> { // Update medication
                String id = lookupManager.lookupEntity(EntityLookup.medicationLookup(medicationService), "Select Medication to Update"); // Selects medication
                if (id == null) return; // Exits if no selection
                Map<String, Object> updates = new HashMap<>(); // Initializes updates map
                System.out.print("Enter field to update (name/sideEffects/benefits): "); // Prompts for field
                String field = scanner.nextLine().trim(); // Gets field
                System.out.print("Enter new value: "); // Prompts for value
                updates.put(field, scanner.nextLine().trim()); // Adds update
                medicationService.updateMedicationFields(id, updates); // Updates medication
                consoleUI.showSuccess("Medication updated successfully"); // Shows success
            }
            case 5 -> { // Delete medication
                String id = lookupManager.lookupEntity(EntityLookup.medicationLookup(medicationService), "Select Medication to Delete"); // Selects medication
                if (id == null) return; // Exits if no selection
                medicationService.deleteMedication(id); // Deletes medication
                consoleUI.showSuccess("Medication deleted successfully"); // Shows success
            }
            default -> { // Invalid operation
                consoleUI.showError("Invalid operation for Medication!"); // Shows error
                logger.warn("Invalid medication operation: " + operation); // Logs warning
            }
        }
    }

    private void handleInsuranceOperation(int operation) { // Handles insurance operations
        switch (operation) { // Routes operation
            case 1 -> { // Create insurance
                Insurance insurance = new Insurance(); // Creates new insurance
                System.out.print("Enter company name: "); // Prompts for company name
                insurance.setCompanyName(scanner.nextLine().trim()); // Sets company name
                System.out.print("Enter address: "); // Prompts for address
                insurance.setAddress(scanner.nextLine().trim()); // Sets address
                System.out.print("Enter phone number: "); // Prompts for phone
                insurance.setPhoneNumber(PhoneUtils.formatPhoneNumber(scanner.nextLine().trim())); // Sets formatted phone
                insuranceService.saveInsurance(insurance); // Saves insurance
                consoleUI.showSuccess("Insurance created successfully"); // Shows success
            }
            case 2 -> handlePagination("Insurances", pageRequest -> insuranceService.getPagedInsurances(pageRequest)); // View all insurances
            case 3 -> { // View single insurance
                String id = lookupManager.lookupEntity(EntityLookup.insuranceLookup(insuranceService), "Select Insurance"); // Selects insurance
                if (id == null) return; // Exits if no selection
                Insurance insurance = insuranceService.fetchInsuranceById(id); // Fetches insurance
                Map<String, Boolean> fields = selectFields("Insurance"); // Gets display fields
                System.out.println(formatItem(insurance, "Insurance", fields)); // Displays insurance
            }
            case 4 -> { // Update insurance
                String id = lookupManager.lookupEntity(EntityLookup.insuranceLookup(insuranceService), "Select Insurance to Update"); // Selects insurance
                if (id == null) return; // Exits if no selection
                Map<String, Object> updates = new HashMap<>(); // Initializes updates map
                System.out.print("Enter field to update (companyName/address/phoneNumber): "); // Prompts for field
                String field = scanner.nextLine().trim(); // Gets field
                System.out.print("Enter new value: "); // Prompts for value
                String value = scanner.nextLine().trim(); // Gets value
                updates.put(field, "phoneNumber".equals(field) ? PhoneUtils.formatPhoneNumber(value) : value); // Adds update
                insuranceService.updateInsuranceFields(id, updates); // Updates insurance
                consoleUI.showSuccess("Insurance updated successfully"); // Shows success
            }
            case 5 -> { // Delete insurance
                String id = lookupManager.lookupEntity(EntityLookup.insuranceLookup(insuranceService), "Select Insurance to Delete"); // Selects insurance
                if (id == null) return; // Exits if no selection
                insuranceService.deleteInsurance(id); // Deletes insurance
                consoleUI.showSuccess("Insurance deleted successfully"); // Shows success
            }
            default -> { // Invalid operation
                consoleUI.showError("Invalid operation for Insurance!"); // Shows error
                logger.warn("Invalid insurance operation: " + operation); // Logs warning
            }
        }
    }

    private void handlePrescriptionOperation(int operation) { // Handles prescription operations
        switch (operation) { // Routes operation
            case 1 -> { // Create prescription
                Prescription prescription = new Prescription(); // Creates new prescription
                System.out.print("Enter prescription date (yyyy-MM-dd): "); // Prompts for date
                prescription.setPrescriptionDate(LocalDate.parse(scanner.nextLine().trim())); // Sets date
                System.out.print("Enter dosage (1-50): "); // Prompts for dosage
                prescription.setDosage(Integer.parseInt(scanner.nextLine().trim())); // Sets dosage
                System.out.print("Enter duration (1-100): "); // Prompts for duration
                prescription.setDuration(Integer.parseInt(scanner.nextLine().trim())); // Sets duration
                System.out.print("Enter comments: "); // Prompts for comments
                prescription.setComments(scanner.nextLine().trim()); // Sets comments
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient"); // Selects patient
                if (patientId == null) return; // Exits if no selection
                prescription.setPatient(patientService.fetchPatientById(patientId)); // Sets patient
                String medicationId = lookupManager.lookupEntity(EntityLookup.medicationLookup(medicationService), "Select Medication"); // Selects medication
                if (medicationId == null) return; // Exits if no selection
                prescription.setMedication(medicationService.fetchMedicationById(medicationId)); // Sets medication
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor"); // Selects doctor
                if (doctorId == null) return; // Exits if no selection
                prescription.setDoctor(doctorService.fetchDoctorById(doctorId)); // Sets doctor
                prescriptionService.savePrescription(prescription); // Saves prescription
                consoleUI.showSuccess("Prescription created successfully"); // Shows success
            }
            case 2 -> handlePagination("Prescriptions", pageRequest -> prescriptionService.getPagedPrescriptions(pageRequest)); // View all prescriptions
            case 3 -> { // View single prescription
                String id = lookupManager.lookupEntity(EntityLookup.prescriptionLookup(prescriptionService), "Select Prescription"); // Selects prescription
                if (id == null) return; // Exits if no selection
                Prescription prescription = prescriptionService.fetchPrescriptionById(id); // Fetches prescription
                Map<String, Boolean> fields = selectFields("Prescription"); // Gets display fields
                System.out.println(formatItem(prescription, "Prescription", fields)); // Displays prescription
            }
            case 4 -> { // Update prescription
                String id = lookupManager.lookupEntity(EntityLookup.prescriptionLookup(prescriptionService), "Select Prescription to Update"); // Selects prescription
                if (id == null) return; // Exits if no selection
                Map<String, Object> updates = new HashMap<>(); // Initializes updates map
                System.out.print("Enter field to update (prescriptionDate/dosage/duration/comments): "); // Prompts for field
                String field = scanner.nextLine().trim(); // Gets field
                System.out.print("Enter new value: "); // Prompts for value
                String value = scanner.nextLine().trim(); // Gets value
                switch (field) { // Handles field update
                    case "prescriptionDate" -> updates.put(field, LocalDate.parse(value)); // Updates date
                    case "dosage", "duration" -> updates.put(field, Integer.valueOf(value)); // Updates numeric fields
                    default -> updates.put(field, value); // Updates string fields
                }
                prescriptionService.updatePrescriptionFields(id, updates); // Updates prescription
                consoleUI.showSuccess("Prescription updated successfully"); // Shows success
            }
            case 5 -> { // Delete prescription
                String id = lookupManager.lookupEntity(EntityLookup.prescriptionLookup(prescriptionService), "Select Prescription to Delete"); // Selects prescription
                if (id == null) return; // Exits if no selection
                prescriptionService.deletePrescription(id); // Deletes prescription
                consoleUI.showSuccess("Prescription deleted successfully"); // Shows success
            }
            default -> { // Invalid operation
                consoleUI.showError("Invalid operation for Prescription!"); // Shows error
                logger.warn("Invalid prescription operation: " + operation); // Logs warning
            }
        }
    }

    private void handleVisitOperation(int operation) { // Handles visit operations
        switch (operation) { // Routes operation
            case 1 -> { // Create visit
                Visit visit = new Visit(); // Creates new visit
                VisitId id = new VisitId(); // Creates visit ID
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient"); // Selects patient
                if (patientId == null) return; // Exits if no selection
                id.setPatientId(patientId); // Sets patient ID
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor"); // Selects doctor
                if (doctorId == null) return; // Exits if no selection
                id.setDoctorId(doctorId); // Sets doctor ID
                System.out.print("Enter visit date (yyyy-MM-dd): "); // Prompts for date
                id.setVisitDate(LocalDate.parse(scanner.nextLine().trim())); // Sets date
                visit.setId(id); // Sets visit ID
                System.out.print("Enter symptoms: "); // Prompts for symptoms
                visit.setSymptoms(scanner.nextLine().trim()); // Sets symptoms
                System.out.print("Enter diagnosis (1-100000): "); // Prompts for diagnosis
                visit.setDiagnosis(Integer.parseInt(scanner.nextLine().trim())); // Sets diagnosis
                visitService.saveVisit(visit); // Saves visit
                consoleUI.showSuccess("Visit created successfully"); // Shows success
            }
            case 2 -> handlePagination("Visits", pageRequest -> visitService.getPagedVisits(pageRequest)); // View all visits
            case 3 -> { // View single visit
                VisitId id = new VisitId(); // Creates visit ID
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient"); // Selects patient
                if (patientId == null) return; // Exits if no selection
                id.setPatientId(patientId); // Sets patient ID
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor"); // Selects doctor
                if (doctorId == null) return; // Exits if no selection
                id.setDoctorId(doctorId); // Sets doctor ID
                System.out.print("Enter visit date (yyyy-MM-dd): "); // Prompts for date
                id.setVisitDate(LocalDate.parse(scanner.nextLine().trim())); // Sets date
                Visit visit = visitService.fetchVisitById(id); // Fetches visit
                Map<String, Boolean> fields = selectFields("Visit"); // Gets display fields
                System.out.println(formatItem(visit, "Visit", fields)); // Displays visit
            }
            case 4 -> { // Update visit
                VisitId id = new VisitId(); // Creates visit ID
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient"); // Selects patient
                if (patientId == null) return; // Exits if no selection
                id.setPatientId(patientId); // Sets patient ID
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor"); // Selects doctor
                if (doctorId == null) return; // Exits if no selection
                id.setDoctorId(doctorId); // Sets doctor ID
                System.out.print("Enter visit date (yyyy-MM-dd): "); // Prompts for date
                id.setVisitDate(LocalDate.parse(scanner.nextLine().trim())); // Sets date
                Map<String, Object> updates = new HashMap<>(); // Initializes updates map
                System.out.print("Enter field to update (symptoms/diagnosis): "); // Prompts for field
                String field = scanner.nextLine().trim(); // Gets field
                System.out.print("Enter new value: "); // Prompts for value
                updates.put(field, "diagnosis".equals(field) ? Integer.valueOf(scanner.nextLine().trim()) : scanner.nextLine().trim()); // Adds update
                visitService.updateVisitFields(id, updates); // Updates visit
                consoleUI.showSuccess("Visit updated successfully"); // Shows success
            }
            case 5 -> { // Delete visit
                VisitId id = new VisitId(); // Creates visit ID
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient"); // Selects patient
                if (patientId == null) return; // Exits if no selection
                id.setPatientId(patientId); // Sets patient ID
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor"); // Selects doctor
                if (doctorId == null) return; // Exits if no selection
                id.setDoctorId(doctorId); // Sets doctor ID
                System.out.print("Enter visit date (yyyy-MM-dd): "); // Prompts for date
                id.setVisitDate(LocalDate.parse(scanner.nextLine().trim())); // Sets date
                visitService.deleteVisit(id); // Deletes visit
                consoleUI.showSuccess("Visit deleted successfully"); // Shows success
            }
            default -> { // Invalid operation
                consoleUI.showError("Invalid operation for Visit!"); // Shows error
                logger.warn("Invalid visit operation: " + operation); // Logs warning
            }
        }
    }

    private void showAnalytics() { // Displays analytics
        long startTime = logger.startOperation("Displaying analytics", Map.of()); // Logs start
        try {
            Map<String, Long> stats = analyticsService.getDashboardStats(); // Fetches analytics stats
            System.out.println("[Analytics]:"); // Prints title
            stats.forEach((key, value) -> System.out.println("  " + key + ": " + value)); // Prints stats
            logger.success("Analytics displayed successfully", startTime); // Logs success
        } catch (Exception e) { // Handles errors
            logger.error("Error fetching analytics", startTime, e, "Analytics display"); // Logs error
            consoleUI.showError("Error fetching analytics: " + e.getMessage()); // Shows error
        }
    }
}