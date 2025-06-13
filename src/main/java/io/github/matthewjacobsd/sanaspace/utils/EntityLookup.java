package io.github.matthewjacobsd.sanaspace.utils;

import io.github.matthewjacobsd.sanaspace.models.*;
import io.github.matthewjacobsd.sanaspace.services.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.function.Function;

public class EntityLookup<T> {
    private final String entityName;
    private final Function<PageRequest, Page<T>> fetchPage;
    private final Function<T, String> displayName;

    public EntityLookup(String entityName, 
                      Function<PageRequest, Page<T>> fetchPage, 
                      Function<T, String> displayName) {
        this.entityName = entityName;
        this.fetchPage = fetchPage;
        this.displayName = displayName;
    }

    public String getEntityName() {
        return entityName;
    }

    public Page<T> fetchPage(PageRequest pageRequest) {
        return fetchPage.apply(pageRequest);
    }

    public String getDisplayName(T entity) {
        return displayName.apply(entity);
    }

    public static EntityLookup<Doctor> doctorLookup(SDoctor doctorService) {
        return new EntityLookup<>(
            "Doctor",
            doctorService::getPagedDoctors,
            d -> d.getFirstName() + " " + d.getLastName()
        );
    }

    public static EntityLookup<Patient> patientLookup(SPatient patientService) {
        return new EntityLookup<>(
            "Patient",
            patientService::getPagedPatients,
            p -> p.getFirstName() + " " + p.getLastName()
        );
    }

    public static EntityLookup<Medication> medicationLookup(SMedication medicationService) {
        return new EntityLookup<>(
            "Medication",
            medicationService::getPagedMedications,
            Medication::getName
        );
    }

    public static EntityLookup<Insurance> insuranceLookup(SInsurance insuranceService) {
        return new EntityLookup<>(
            "Insurance",
            insuranceService::getPagedInsurances,
            Insurance::getCompanyName
        );
    }

    public static EntityLookup<Prescription> prescriptionLookup(SPrescription prescriptionService) {
        return new EntityLookup<>(
            "Prescription",
            prescriptionService::getPagedPrescriptions,
            p -> p.getId() + " (Date: " + p.getPrescriptionDate().toString() + ")"
        );
    }
}