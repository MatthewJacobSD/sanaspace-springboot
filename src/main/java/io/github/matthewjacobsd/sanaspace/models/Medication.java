package io.github.matthewjacobsd.sanaspace.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// Table structure
@Entity
@Table(name = "medications")

// Lombok annotations
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medication {
    
    // Unique Identifier (UUID)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    // name
    @Size(min = 3, max = 50, message = "Medication name must be between 3 and 50 characters")
    @Column(name = "name")
    private String name;

    // side_effects
    @Size(min = 10, max = 1000, message = "Side effects must be between 10 and 1000 characters")
    @Column(name = "side_effects")
    private String sideEffects;

    // benefits
    @Size(min = 10, max = 1000, message = "Benefits must be between 10 and 1000 characters")
    @Column(name = "benefits")
    private String benefits;

    // relationships

    // One-to-Many relationship with Prescription
    @Builder.Default
    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Prescription> prescriptions = new ArrayList<>();
}
