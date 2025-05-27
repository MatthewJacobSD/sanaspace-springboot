package io.github.matthewjacobsd.sanaspace.repositories;

import io.github.matthewjacobsd.sanaspace.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Patient entity database operations
public interface RPatient extends JpaRepository<Patient, String> {
}