package io.github.matthewjacobsd.sanaspace.repositories;

import io.github.matthewjacobsd.sanaspace.models.Visit;
import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Visit entity database operations
public interface RVisit extends JpaRepository<Visit, VisitId> {
}