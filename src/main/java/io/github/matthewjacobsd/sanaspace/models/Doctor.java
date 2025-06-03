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
        General, 
        Ophthalmology,
        Oncologists,
        Emergency,
        Anaesthetists,
        IntensiveCare,
        Cardiology
    }

    // Enumeration for experience
    public enum Experience {
        Novice,
        Junior,
        Senior,
        Expert
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
