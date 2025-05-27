package io.github.matthewjacobsd.sanaspace.exceptions;

public class ExpDoctor extends RuntimeException {
    public ExpDoctor(String id) {
        super("Doctor not found with id: " + id);
    }
}