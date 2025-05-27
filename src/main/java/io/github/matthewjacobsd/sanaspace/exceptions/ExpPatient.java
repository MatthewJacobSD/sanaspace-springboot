package io.github.matthewjacobsd.sanaspace.exceptions;

public class ExpPatient extends RuntimeException {
    public ExpPatient(String id) {
        super("Patient not found with id: " + id);
    }
}