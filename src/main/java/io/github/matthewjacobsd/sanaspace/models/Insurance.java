package io.github.matthewjacobsd.sanaspace.models;

import io.github.matthewjacobsd.sanaspace.utils.PhoneUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// Table structure
@Entity
@Table(name = "insurances")

// Lombok annotations
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Insurance {

    // Unique Identifier (UUID)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    // compoany_name
    @NotNull
    @Column(name = "company_name")
    private String companyName;

    // address
    @Column(name = "address")
    private String address;

    // phone_number (min 10)
    @Size(min = 10, message = "Phone number must be at least 10 characters")
    @Pattern(message = "Invalid phone number format", regexp = "^\\d{3}-?\\d{3}-?\\d{4}$")
    @Column(name = "phone_number")
    private String phoneNumber;

    // relationships

    // One-to-Many relationship with Patient
    @Builder.Default
    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Patient> patients = new ArrayList<>();

    // auto-update phone pattern
    @PrePersist
    @PreUpdate
    private void updatePhoneNumber() {
        this.phoneNumber = PhoneUtils.formatPhoneNumber(phoneNumber);
    }

}
