package io.github.matthewjacobsd.sanaspace.repositories;

import io.github.matthewjacobsd.sanaspace.models.Doctor;
import io.github.matthewjacobsd.sanaspace.models.Visit;
import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;

// Repository for Visit entity database operations
public interface RVisit extends JpaRepository<Visit, VisitId> {
    @Query("SELECT v.doctor " +
           "FROM Visit v " +
           "WHERE v.id.patientId = :patientId " +
           "GROUP BY v.doctor " +
           "ORDER BY COUNT(v) DESC")
    Page<Doctor> findMainDoctorByPatientId(@Param("patientId") String patientId, Pageable pageable);
}