package io.github.matthewjacobsd.sanaspace.repositories;

import io.github.matthewjacobsd.sanaspace.models.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Medication entity database operations
public interface RMedication extends JpaRepository<Medication, String> {
}