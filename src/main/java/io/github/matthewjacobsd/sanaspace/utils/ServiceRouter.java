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

@Component
@RequiredArgsConstructor
public class ServiceRouter implements CommandLineRunner {

    private final SDoctor doctorService;
    private final SPatient patientService;
    private final SMedication medicationService;
    private final SInsurance insuranceService;
    private final SPrescription prescriptionService;
    private final SVisit visitService;
    private final CAnalytics analyticsService;
    private final ConsoleUI consoleUI;
    private final LookupManager lookupManager;
    private final Scanner scanner = new Scanner(System.in);
    private final LoggerUtil logger = new LoggerUtil(ServiceRouter.class);

    // Starts the application loop
    @Override
    public void run(String... args) throws Exception {
        long startTime = logger.startOperation("Starting application loop", Map.of());
        consoleUI.displayWelcome();
        displayMainMenuLoop();
        consoleUI.displayExit();
        logger.success("Application loop completed successfully", startTime);
    }

    // Displays main menu and handles choices
    private void displayMainMenuLoop() {
        while (true) {
            consoleUI.displayMainMenu();
            int choice = consoleUI.getUserChoice();
            if (choice == -1)
                continue; // Skip invalid input
            if (choice == 0)
                return; // Exit
            route(choice);
        }
    }

    // Routes main menu choice to appropriate entity menu or analytics
    public void route(int choice) {
        long startTime = logger.startOperation("Routing menu choice", Map.of("choice", choice));
        try {
            switch (choice) {
                case 1 -> displayEntityMenuLoop("Doctor", ConsoleUI.DOCTOR_EMOJI);
                case 2 -> displayEntityMenuLoop("Patient", ConsoleUI.PATIENT_EMOJI);
                case 3 -> displayEntityMenuLoop("Medication", ConsoleUI.MEDICATION_EMOJI);
                case 4 -> displayEntityMenuLoop("Insurance", ConsoleUI.INSURANCE_EMOJI);
                case 5 -> displayEntityMenuLoop("Prescription", ConsoleUI.PRESCRIPTION_EMOJI);
                case 6 -> displayEntityMenuLoop("Visit", ConsoleUI.VISIT_EMOJI);
                case 7 -> showAnalytics();
                default -> {
                    consoleUI.showError("Invalid choice!");
                    logger.warn("Invalid menu choice: " + choice);
                }
            }
            logger.success("Menu choice routed successfully", startTime);
        } catch (Exception e) {
            logger.error("Failed to route menu choice", startTime, e, "Choice: " + choice);
            consoleUI.showError("Error processing choice: " + e.getMessage());
        }
    }

    // Displays entity menu and handles operations
    private void displayEntityMenuLoop(String entityName, String emoji) {
        while (true) {
            consoleUI.displayEntityMenu(entityName, emoji);
            int choice = consoleUI.getUserChoice();
            if (choice == -1)
                continue; // Skip invalid input
            if (choice == 0)
                return; // Back to main menu
            handleEntityOperation(entityName.toLowerCase(), choice);
        }
    }

    // Routes entity-specific operation to appropriate handler
    public void handleEntityOperation(String entity, int operation) {
        long startTime = logger.startOperation("Handling " + entity + " operation",
                Map.of("entity", entity, "operation", operation));
        try {
            switch (entity) {
                case "doctor" -> handleDoctorOperation(operation);
                case "patient" -> handlePatientOperation(operation);
                case "medication" -> handleMedicationOperation(operation);
                case "insurance" -> handleInsuranceOperation(operation);
                case "prescription" -> handlePrescriptionOperation(operation);
                case "visit" -> handleVisitOperation(operation);
                default -> {
                    consoleUI.showError("Invalid entity!");
                    logger.warn("Invalid entity: " + entity);
                }
            }
            logger.success(entity + " operation completed successfully", startTime);
        } catch (Exception e) {
            logger.error("Failed to handle " + entity + " operation", startTime, e, "Operation: " + operation);
            consoleUI.showError("Error: " + e.getMessage());
        }
    }

    // Helper method for pagination and navigation
    private <T> void handlePagination(String entityName, Function<PageRequest, Page<T>> fetchPage,
            Function<T, String> displayItem) {
        // Prompt for items per page
        int pageSize = 10; // Default
        consoleUI.showInfo("Enter items per page (e.g., 5, 10, 20) or press Enter for default (10):");
        String input = scanner.nextLine();
        try {
            if (!input.isEmpty()) {
                pageSize = Integer.parseInt(input);
                if (pageSize <= 0) {
                    consoleUI.showError("Page size must be positive!");
                    pageSize = 10;
                }
            }
        } catch (NumberFormatException e) {
            consoleUI.showError("Invalid number! Using default page size (10).");
        }

        int currentPage = 0; // Start at page 0 (first page)
        while (true) {
            // Fetch the page
            Page<T> page = fetchPage.apply(PageRequest.of(currentPage, pageSize));
            PaginationResponse<T> response = new PaginationResponse<>(page);

            // Display items
            consoleUI.showInfo(entityName + " List (Page " + (response.getPage() + 1) + " of "
                    + response.getTotalPages() + ", Total: " + response.getTotalItems() + ")");
            if (response.getItems().isEmpty()) {
                consoleUI.showWarning("No " + entityName.toLowerCase() + "s found.");
            } else {
                response.getItems().forEach(item -> System.out.println(displayItem.apply(item)));
            }

            // Prompt for navigation
            consoleUI.showInfo("Enter 'n' for next page, 'p' for previous page, or 'q' to quit:");
            String nav = scanner.nextLine().toLowerCase();
            if (nav.equals("q")) {
                break; // Exit pagination
            } else if (nav.equals("n") && currentPage < page.getTotalPages() - 1) {
                currentPage++; // Next page
            } else if (nav.equals("p") && currentPage > 0) {
                currentPage--; // Previous page
            } else if (!nav.isEmpty()) {
                consoleUI.showError("Invalid input! Use 'n', 'p', or 'q'.");
            }
        }
    }

    // Helper method to display and select enum values
    private <E extends Enum<E>> E selectEnum(Class<E> enumClass, String fieldName) {
        E[] values = enumClass.getEnumConstants();
        consoleUI.showInfo("Available " + fieldName + " options:");
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i].name());
        }
        System.out.print("Choose an option (1-" + values.length + "): ");
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= values.length) {
                    return values[choice - 1];
                } else {
                    consoleUI.showError("Invalid choice! Pick a number between 1 and " + values.length);
                }
            } catch (NumberFormatException e) {
                consoleUI.showError("Invalid input! Enter a number.");
            }
        }
    }

    // Handles doctor CRUD operations
    private void handleDoctorOperation(int operation) {
        switch (operation) {
            case 1 -> { // Create doctor
                Doctor doctor = new Doctor();
                System.out.print("Enter first name: ");
                doctor.setFirstName(scanner.nextLine());
                System.out.print("Enter last name: ");
                doctor.setLastName(scanner.nextLine());
                System.out.print("Enter address: ");
                doctor.setAddress(scanner.nextLine());
                System.out.print("Enter email: ");
                doctor.setEmail(scanner.nextLine());
                doctor.setSpecialization(selectEnum(Doctor.Specialization.class, "Specialization"));
                doctor.setExperience(selectEnum(Doctor.Experience.class, "Experience"));
                doctorService.saveDoctor(doctor);
                consoleUI.showSuccess("Doctor created successfully");
            }
            case 2 -> { // View all doctors
                handlePagination(
                        "Doctors",
                        pageRequest -> doctorService.getPagedDoctors(pageRequest),
                        doctor -> "⚕️ Doctor: " + doctor.getFirstName() + " " + doctor.getLastName());
            }
            case 3 -> { // View single doctor
                String id = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor");
                if (id == null)
                    return;
                Doctor doctor = doctorService.fetchDoctorById(id);
                System.out.println("⚕️ Doctor: " + doctor.getFirstName() + " " + doctor.getLastName() + ", Email: "
                        + doctor.getEmail());
            }
            case 4 -> { // Update doctor
                String id = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService),
                        "Select Doctor to Update");
                if (id == null)
                    return;
                Map<String, Object> updates = new HashMap<>();
                System.out
                        .print("Enter field to update (firstName/lastName/address/email/specialization/experience): ");
                String field = scanner.nextLine();
                switch (field) {
                    case "specialization" ->
                        updates.put(field, selectEnum(Doctor.Specialization.class, "Specialization").name());
                    case "experience" -> 
                        updates.put(field, selectEnum(Doctor.Experience.class, "Experience").name());
                    default -> {
                        System.out.print("Enter new value: ");
                        updates.put(field, scanner.nextLine());
                    }
                }
                doctorService.updateDoctorFields(id, updates);
                consoleUI.showSuccess("Doctor updated successfully");
            }
            case 5 -> { // Delete doctor
                String id = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService),
                        "Select Doctor to Delete");
                if (id == null)
                    return;
                doctorService.deleteDoctor(id);
                consoleUI.showSuccess("Doctor deleted successfully");
            }
        }
    }

    // Handles patient CRUD operations
    private void handlePatientOperation(int operation) {
        switch (operation) {
            case 1 -> {
                Patient patient = new Patient();
                System.out.print("Enter first name: ");
                patient.setFirstName(scanner.nextLine());
                System.out.print("Enter last name: ");
                patient.setLastName(scanner.nextLine());
                System.out.print("Enter postcode: ");
                patient.setPostcode(scanner.nextLine());
                System.out.print("Enter address: ");
                patient.setAddress(scanner.nextLine());
                System.out.print("Enter phone number: ");
                patient.setPhoneNumber(PhoneUtils.formatPhoneNumber(scanner.nextLine()));
                System.out.print("Enter email: ");
                patient.setEmail(scanner.nextLine());
                String insuranceId = lookupManager.lookupEntity(EntityLookup.insuranceLookup(insuranceService),
                        "Select Insurance (or 0 to skip)");
                if (insuranceId != null) {
                    patient.setInsurance(insuranceService.fetchInsuranceById(insuranceId));
                }
                patientService.savePatient(patient);
                consoleUI.showSuccess("Patient created successfully");
            }
            case 2 -> { // View all patients
                handlePagination(
                        "Patients",
                        pageRequest -> patientService.getPagedPatients(pageRequest),
                        patient -> "🩺 Patient: " + patient.getFirstName() + " " + patient.getLastName());
            }
            case 3 -> {
                String id = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService), "Select Patient");
                if (id == null)
                    return;
                Patient patient = patientService.fetchPatientById(id);
                System.out.println("🩺 Patient: " + patient.getFirstName() + " " + patient.getLastName() + ", Email: "
                        + patient.getEmail());
            }
            case 4 -> {
                String id = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService),
                        "Select Patient to Update");
                if (id == null)
                    return;
                Map<String, Object> updates = new HashMap<>();
                System.out.print("Enter field to update (firstName/lastName/postcode/address/phoneNumber/email): ");
                String field = scanner.nextLine();
                System.out.print("Enter new value: ");
                String value = scanner.nextLine();
                updates.put(field, "phoneNumber".equals(field) ? PhoneUtils.formatPhoneNumber(value) : value);
                patientService.updatePatientFields(id, updates);
                consoleUI.showSuccess("Patient updated successfully");
            }
            case 5 -> {
                String id = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService),
                        "Select Patient to Delete");
                if (id == null)
                    return;
                patientService.deletePatient(id);
                consoleUI.showSuccess("Patient deleted successfully");
            }
        }
    }

    // Handles medication CRUD operations
    private void handleMedicationOperation(int operation) {
        switch (operation) {
            case 1 -> {
                Medication medication = new Medication();
                System.out.print("Enter medication name: ");
                medication.setName(scanner.nextLine());
                System.out.print("Enter side effects: ");
                medication.setSideEffects(scanner.nextLine());
                System.out.print("Enter benefits: ");
                medication.setBenefits(scanner.nextLine());
                medicationService.saveMedication(medication);
                consoleUI.showSuccess("Medication created successfully");
            }
            case 2 -> { // View all medications
                handlePagination(
                        "Medications",
                        pageRequest -> medicationService.getPagedMedications(pageRequest),
                        medication -> "💊 Medication: " + medication.getName());
            }
            case 3 -> {
                String id = lookupManager.lookupEntity(EntityLookup.medicationLookup(medicationService),
                        "Select Medication");
                if (id == null)
                    return;
                Medication medication = medicationService.fetchMedicationById(id);
                System.out.println("💊 Medication: " + medication.getName());
            }
            case 4 -> {
                String id = lookupManager.lookupEntity(EntityLookup.medicationLookup(medicationService),
                        "Select Medication to Update");
                if (id == null)
                    return;
                Map<String, Object> updates = new HashMap<>();
                System.out.print("Enter field to update (name/sideEffects/benefits): ");
                String field = scanner.nextLine();
                System.out.print("Enter new value: ");
                updates.put(field, scanner.nextLine());
                medicationService.updateMedicationFields(id, updates);
                consoleUI.showSuccess("Medication updated successfully");
            }
            case 5 -> {
                String id = lookupManager.lookupEntity(EntityLookup.medicationLookup(medicationService),
                        "Select Medication to Delete");
                if (id == null)
                    return;
                medicationService.deleteMedication(id);
                consoleUI.showSuccess("Medication deleted successfully");
            }
        }
    }

    // Handles insurance CRUD operations
    private void handleInsuranceOperation(int operation) {
        switch (operation) {
            case 1 -> {
                Insurance insurance = new Insurance();
                System.out.print("Enter company name: ");
                insurance.setCompanyName(scanner.nextLine());
                System.out.print("Enter address: ");
                insurance.setAddress(scanner.nextLine());
                System.out.print("Enter phone number: ");
                insurance.setPhoneNumber(PhoneUtils.formatPhoneNumber(scanner.nextLine()));
                insuranceService.saveInsurance(insurance);
                consoleUI.showSuccess("Insurance created successfully");
            }
            case 2 -> { // View all insurances
                handlePagination(
                        "Insurances",
                        pageRequest -> insuranceService.getPagedInsurances(pageRequest),
                        insurance -> "🛡️ Insurance: " + insurance.getCompanyName());
            }
            case 3 -> {
                String id = lookupManager.lookupEntity(EntityLookup.insuranceLookup(insuranceService),
                        "Select Insurance");
                if (id == null)
                    return;
                Insurance insurance = insuranceService.fetchInsuranceById(id);
                System.out.println("🛡️ Insurance: " + insurance.getCompanyName());
            }
            case 4 -> {
                String id = lookupManager.lookupEntity(EntityLookup.insuranceLookup(insuranceService),
                        "Select Insurance to Update");
                if (id == null)
                    return;
                Map<String, Object> updates = new HashMap<>();
                System.out.print("Enter field to update (companyName/address/phoneNumber): ");
                String field = scanner.nextLine();
                System.out.print("Enter new value: ");
                String value = scanner.nextLine();
                updates.put(field, "phoneNumber".equals(field) ? PhoneUtils.formatPhoneNumber(value) : value);
                insuranceService.updateInsuranceFields(id, updates);
                consoleUI.showSuccess("Insurance updated successfully");
            }
            case 5 -> {
                String id = lookupManager.lookupEntity(EntityLookup.insuranceLookup(insuranceService),
                        "Select Insurance to Delete");
                if (id == null)
                    return;
                insuranceService.deleteInsurance(id);
                consoleUI.showSuccess("Insurance deleted successfully");
            }
        }
    }

    // Handles prescription CRUD operations
    private void handlePrescriptionOperation(int operation) {
        switch (operation) {
            case 1 -> {
                Prescription prescription = new Prescription();
                System.out.print("Enter prescription date (yyyy-MM-dd): ");
                prescription.setPrescriptionDate(LocalDate.parse(scanner.nextLine()));
                System.out.print("Enter dosage (1-50): ");
                prescription.setDosage(Integer.parseInt(scanner.nextLine()));
                System.out.print("Enter duration (1-100): ");
                prescription.setDuration(Integer.parseInt(scanner.nextLine()));
                System.out.print("Enter comments: ");
                prescription.setComments(scanner.nextLine());
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService),
                        "Select Patient");
                if (patientId == null)
                    return;
                prescription.setPatient(patientService.fetchPatientById(patientId));
                String medicationId = lookupManager.lookupEntity(EntityLookup.medicationLookup(medicationService),
                        "Select Medication");
                if (medicationId == null)
                    return;
                prescription.setMedication(medicationService.fetchMedicationById(medicationId));
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor");
                if (doctorId == null)
                    return;
                prescription.setDoctor(doctorService.fetchDoctorById(doctorId));
                prescriptionService.savePrescription(prescription);
                consoleUI.showSuccess("Prescription created successfully");
            }
            case 2 -> { // View all prescriptions
                handlePagination(
                        "Prescriptions",
                        pageRequest -> prescriptionService.getPagedPrescriptions(pageRequest),
                        prescription -> "📝 Prescription: " + prescription.getId());
            }
            case 3 -> {
                String id = lookupManager.lookupEntity(EntityLookup.prescriptionLookup(prescriptionService),
                        "Select Prescription");
                if (id == null)
                    return;
                Prescription prescription = prescriptionService.fetchPrescriptionById(id);
                System.out.println(
                        "📝 Prescription: " + prescription.getId() + ", Date: " + prescription.getPrescriptionDate());
            }
            case 4 -> {
                String id = lookupManager.lookupEntity(EntityLookup.prescriptionLookup(prescriptionService),
                        "Select Prescription to Update");
                if (id == null)
                    return;
                Map<String, Object> updates = new HashMap<>();
                System.out.print("Enter field to update (prescriptionDate/dosage/duration/comments): ");
                String field = scanner.nextLine();
                System.out.print("Enter new value: ");
                String value = scanner.nextLine();
                switch (field) {
                    case "prescriptionDate" -> updates.put(field, LocalDate.parse(value));
                    case "dosage", "duration" -> updates.put(field, Integer.valueOf(value));
                    default -> updates.put(field, value);
                }
                prescriptionService.updatePrescriptionFields(id, updates);
                consoleUI.showSuccess("Prescription updated successfully");
            }
            case 5 -> {
                String id = lookupManager.lookupEntity(EntityLookup.prescriptionLookup(prescriptionService),
                        "Select Prescription to Delete");
                if (id == null)
                    return;
                prescriptionService.deletePrescription(id);
                consoleUI.showSuccess("Prescription deleted successfully");
            }
        }
    }

    // Handles visit CRUD operations
    private void handleVisitOperation(int operation) {
        switch (operation) {
            case 1 -> {
                Visit visit = new Visit();
                VisitId id = new VisitId();
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService),
                        "Select Patient");
                if (patientId == null)
                    return;
                id.setPatientId(patientId);
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor");
                if (doctorId == null)
                    return;
                id.setDoctorId(doctorId);
                System.out.print("Enter visit date (yyyy-MM-dd): ");
                id.setVisitDate(LocalDate.parse(scanner.nextLine()));
                visit.setId(id);
                System.out.print("Enter symptoms: ");
                visit.setSymptoms(scanner.nextLine());
                System.out.print("Enter diagnosis (1-100000): ");
                visit.setDiagnosis(Integer.parseInt(scanner.nextLine()));
                visitService.saveVisit(visit);
                consoleUI.showSuccess("Visit created successfully");
            }
            case 2 -> { // View all visits
                handlePagination(
                        "Visits",
                        pageRequest -> visitService.getPagedVisits(pageRequest),
                        visit -> "📅 Visit: " + visit.getId().getVisitDate() + ", Patient: "
                                + visit.getId().getPatientId());
            }
            case 3 -> {
                VisitId id = new VisitId();
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService),
                        "Select Patient");
                if (patientId == null)
                    return;
                id.setPatientId(patientId);
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor");
                if (doctorId == null)
                    return;
                id.setDoctorId(doctorId);
                System.out.print("Enter visit date (yyyy-MM-dd): ");
                id.setVisitDate(LocalDate.parse(scanner.nextLine()));
                Visit visit = visitService.fetchVisitById(id);
                System.out.println("📅 Visit: " + visit.getId().getVisitDate() + ", Symptoms: " + visit.getSymptoms());
            }
            case 4 -> {
                VisitId id = new VisitId();
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService),
                        "Select Patient");
                if (patientId == null)
                    return;
                id.setPatientId(patientId);
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor");
                if (doctorId == null)
                    return;
                id.setDoctorId(doctorId);
                System.out.print("Enter visit date (yyyy-MM-dd): ");
                id.setVisitDate(LocalDate.parse(scanner.nextLine()));
                Map<String, Object> updates = new HashMap<>();
                System.out.print("Enter field to update (symptoms/diagnosis): ");
                String field = scanner.nextLine();
                System.out.print("Enter new value: ");
                updates.put(field,
                        "diagnosis".equals(field) ? Integer.valueOf(scanner.nextLine()) : scanner.nextLine());
                visitService.updateVisitFields(id, updates);
                consoleUI.showSuccess("Visit updated successfully");
            }
            case 5 -> {
                VisitId id = new VisitId();
                String patientId = lookupManager.lookupEntity(EntityLookup.patientLookup(patientService),
                        "Select Patient");
                if (patientId == null)
                    return;
                id.setPatientId(patientId);
                String doctorId = lookupManager.lookupEntity(EntityLookup.doctorLookup(doctorService), "Select Doctor");
                if (doctorId == null)
                    return;
                id.setDoctorId(doctorId);
                System.out.print("Enter visit date (yyyy-MM-dd): ");
                id.setVisitDate(LocalDate.parse(scanner.nextLine()));
                visitService.deleteVisit(id);
                consoleUI.showSuccess("Visit deleted successfully");
            }
        }
    }

    // Displays analytics dashboard
    private void showAnalytics() {
        long startTime = logger.startOperation("Displaying analytics", Map.of());
        try {
            Map<String, Long> stats = analyticsService.getDashboardStats();
            System.out.println("📊 Analytics:");
            stats.forEach((key, value) -> System.out.println("  " + key + ": " + value));
            logger.success("Analytics displayed successfully", startTime);
        } catch (Exception e) {
            logger.error("Error fetching analytics", startTime, e, "Analytics display");
            consoleUI.showError("Error fetching analytics: " + e.getMessage());
        }
    }
}