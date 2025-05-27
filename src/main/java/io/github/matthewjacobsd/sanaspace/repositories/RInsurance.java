package io.github.matthewjacobsd.sanaspace.repositories;

import io.github.matthewjacobsd.sanaspace.models.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Insurance entity database operations
public interface RInsurance extends JpaRepository<Insurance, String> {
}