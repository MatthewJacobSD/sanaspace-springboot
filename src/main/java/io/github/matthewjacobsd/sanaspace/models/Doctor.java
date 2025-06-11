package io.github.matthewjacobsd.sanaspace.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// Table structure
@Entity
@Table(name = "doctors")

// Lombok annotations
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Doctor {
    
    // Unique Identifier (UUID)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    // firstname
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    // lastname
    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    @Column(name = "last_name", length = 20)
    private String lastName;

    // address
    @Size(min = 10, max = 100, message = "Address must be between 10 and 100 characters")
    @Column(name = "address", length = 100)
    private String address;

    // email
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    // specialization(opional, has default value)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "specialization")
    private Specialization specialization = Specialization.General;

    // experience(optional, has default value)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "experience")
    private Experience experience = Experience.Novice;

    // Checks doctor if not default
    @Transient
    public boolean isSpecialist() {
        return specialization != Specialization.General;
    }

    // enums

// Enumeration for specialization
public enum Specialization {
    General,           // Primary and general healthcare
    Ophthalmology,     // Eye and vision care
    Oncologists,      // Cancer treatment and research
    Emergency,        // Acute and urgent care
    Anaesthetists,    // Anesthesia and pain management
    IntensiveCare,    // Critical care for severe conditions
    Cardiology,       // Heart and cardiovascular system
    Neurology,       // Brain and nervous system disorders
    Orthopedics,     // Musculoskeletal system and injuries
    Pediatrics,      // Medical care for children
    Dermatology,     // Skin, hair, and nail conditions
    Gastroenterology, // Digestive system disorders
    Psychiatry,      // Mental health and behavioral disorders
    Radiology,       // Medical imaging and diagnostics
    Surgery,         // Surgical procedures and interventions
    Endocrinology,   // Hormone and metabolic disorders nd reproductive system
}

// Enumeration for experience
public enum Experience {
    Novice,      // Beginner, less than 1 year of experience
    Intern,      // Entry-level, 0-1 years in training
    Resident,    // 1-3 years, post-graduate training
    Junior,      // 3-5 years, early career professional
    MidLevel,    // 5-10 years, established practitioner
    Senior,      // 10+ years, highly experienced
    Consultant,  // Expert leading teams or projects
    Specialist,  // Focused expertise in a specific field
    Expert       // Advanced mastery, 15+ years or recognized authority
}

    // relationships

    // One-to-Many relationship with Prescription
    @Builder.Default
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Prescription> prescriptions = new ArrayList<>();

    // One-to-Many relationship with Visit
    @Builder.Default
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Visit> visits = new ArrayList<>();

}
