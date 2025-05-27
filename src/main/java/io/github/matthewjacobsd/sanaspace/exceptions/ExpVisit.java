package io.github.matthewjacobsd.sanaspace.exceptions;

import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;

public class ExpVisit extends RuntimeException {
    public ExpVisit(VisitId id) {
        super("Visit not found with ID: " + id);
    }
}