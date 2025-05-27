package io.github.matthewjacobsd.sanaspace.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    // lastname
    @Column(name = "last_name", length = 50)
    private String lastName;

    // address
    @Column(name = "address", length = 100)
    private String address;

    // email
    @NotNull
    @Email(
        message = "Invalid email address",
        regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    // specialization(opional, has default value)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "specialization")
    private Specialization specialization = Specialization.GENERAL;

    // experience(optional, has default value)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "experience")
    private Experience experience = Experience.NOVICE;

    // Checks doctor if not default
    @Transient
    public boolean isSpecialist() {
        return specialization != Specialization.GENERAL;
    }

    // enums

    // Enumeration for specialization
    public enum Specialization {
        GENERAL, 
        OPHTHALMOLOGY,
        ONCOLOGISTS,
        EMERGENCY,
        ANAESTHETISTS,
        INTENSIVECARE,
        CARDIOLOGY
    }

    // Enumeration for experience
    public enum Experience {
        NOVICE,
        JUNIOR,
        SENIOR,
        EXPERT
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
