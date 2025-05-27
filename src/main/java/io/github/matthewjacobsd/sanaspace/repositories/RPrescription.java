package io.github.matthewjacobsd.sanaspace.repositories;

import io.github.matthewjacobsd.sanaspace.models.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Prescription entity database operations
public interface RPrescription extends JpaRepository<Prescription, String> {
}