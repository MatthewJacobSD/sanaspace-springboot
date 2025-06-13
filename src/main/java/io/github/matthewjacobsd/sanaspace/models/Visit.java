package io.github.matthewjacobsd.sanaspace.models;

import io.github.matthewjacobsd.sanaspace.models.keys.VisitId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

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

    // symptoms
    @Column(name = "symptoms", length = 255)
    private String symptoms;

    // diagnosis (min 1)
    @Min(1)
    @Max(100000)
    @Column(name = "diagnosis")
    private int diagnosis;
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
}