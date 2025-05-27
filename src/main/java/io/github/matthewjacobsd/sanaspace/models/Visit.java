package io.github.matthewjacobsd.sanaspace.models;

import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

// Table structure
@Entity
@Table(name = "visits")

// Lombok annotations
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Visit {
    
    // Composite key ( visit_date, patient_id, doctor_id )
    @EmbeddedId
    private VisitId id;

    // visit_date
    @Transient
    private LocalDate visitDate;

    // symptoms
    @Column(name = "symptoms")
    private String symptoms;

    // diagnosis
    @Column(name = "diagnosis")
    private int diagnosis;

    // auto completes the visit date based on the visit date composite key
    
    // Calculate visit date from composite key
    public LocalDate getVisitDate() {
        return id != null ? id.getVisitDate() : null;
    }

    // Update visit date in composite key
    public void setVisitDate(LocalDate visitDate) {
        if (id == null) {
            id = new VisitId();
        }
        id.setVisitDate(visitDate);
    }

    // relationships

    // Many-to-One relationship with Patient
    @ManyToOne
    @JoinColumn(name = "patient_id", insertable = false, updatable = false, referencedColumnName = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Patient patient;

    // Many-to-One relationship with Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false, referencedColumnName = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Doctor doctor;

    // Ensure proper initialization
    @PostLoad
    private void postLoad() {
        this.visitDate = getVisitDate();
    }
}
