package io.github.matthewjacobsd.sanaspace.exceptions;

public class ExpMedication extends RuntimeException {
    public ExpMedication(String id) {
        super("Medication not found with id: " + id);
    }
}