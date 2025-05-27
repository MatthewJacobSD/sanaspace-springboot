package io.github.matthewjacobsd.sanaspace.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Column(name = "name")
    private String name;

    // side_effects
    @Column(name = "side_effects")
    private String sideEffects;

    // benefits
    @Column(name = "benefits")
    private String benefits;

    // relationships

    // One-to-Many relationship with Prescription
    @Builder.Default
    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Prescription> prescriptions = new ArrayList<>();
}
