package io.github.matthewjacobsd.sanaspace.exceptions;

public class ExpPrescription extends RuntimeException {
    public ExpPrescription(String id) {
        super("Prescription not found with id: " + id);
    }
}