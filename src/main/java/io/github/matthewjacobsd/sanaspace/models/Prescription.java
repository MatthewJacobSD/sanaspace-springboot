package io.github.matthewjacobsd.sanaspace.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

// Table structure
@Entity
@Table(name = "prescriptions")

// Lombok annotations
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prescription {
    
    // Unique Identifier (UUID)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    // prescription_date
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "prescription_date")
    private LocalDate prescriptionDate;

    // dosage amount (min 1)
    @Size(min = 1, max = 50, message = "Dosage must be between 1 and 50 characters")
    @Column(name = "dosage_amount")
    private String dosage;

    // duration (in days) (min 1)
    @Size(min = 1, max = 50, message = "Duration must be between 1 and 50 characters")
    @Column(name = "duration")
    private String duration;

    // comments
    @Size(min = 10, max = 1000, message = "Comments must be between 10 and 1000 characters")
    @Column(name = "comments", length = 1000)
    private String comments;

    // relationships

    // Many-to-One relationship with Patient
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Patient patient;

    // Many-to-One relationship with Medication
    @ManyToOne
    @JoinColumn(name = "medication_id", referencedColumnName = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Medication medication;

    // Many-to-One relationship with Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Doctor doctor;
}
