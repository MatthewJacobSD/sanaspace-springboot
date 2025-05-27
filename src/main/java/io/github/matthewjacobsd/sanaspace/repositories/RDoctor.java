package io.github.matthewjacobsd.sanaspace.repositories;

import io.github.matthewjacobsd.sanaspace.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Doctor entity database operations
public interface RDoctor extends JpaRepository<Doctor, String> {
}