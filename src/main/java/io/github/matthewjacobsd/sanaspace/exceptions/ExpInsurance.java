package io.github.matthewjacobsd.sanaspace.exceptions;

public class ExpInsurance extends RuntimeException {
    public ExpInsurance(String id) {
        super("Insurance not found with id: " + id);
    }
}