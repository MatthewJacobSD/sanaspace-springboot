package io.github.matthewjacobsd.sanaspace.models;

import io.github.matthewjacobsd.sanaspace.utils.PhoneUtils;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Table structure
@Entity
@Table(name = "patients")

// Lombok annotations
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {

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

    // postcode
    @Column(name = "postcode")
    private String postcode;

    // address
    @Size(min = 10, max = 100, message = "Address must be between 10 and 100 characters")
    @Column(name = "address")
    private String address;

    // phone_number (min 10)
    @Size(min = 10, message = "Phone number must be at least 10 characters")
    @Pattern(message = "Invalid phone number format", regexp = "^\\d{3}-?\\d{3}-?\\d{4}$")
    @Column(name = "phone_number")
    private String phoneNumber;

    // email
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Column(name = "email", nullable = false)
    private String email;

    // Highlight insurance if patient has one
    @Builder.Default
    @Column(name = "patient_insurance")
    private boolean isInsured = false;

    // relationships

    // One-to-Many relationship with Prescription
    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Prescription> prescriptions = new ArrayList<>();

    // One-to-Many relationship with Visit
    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Visit> visits = new ArrayList<>();

    // Many-to-One relationship with Insurance
    @ManyToOne
    @JoinColumn(name = "insurance_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Insurance insurance;

    // auto-refresh insurance status & auto-update phone pattern
    @PrePersist
    @PreUpdate
    private void updateInsuranceStatus() {
        this.isInsured = insurance != null;
        this.phoneNumber = PhoneUtils.formatPhoneNumber(phoneNumber);
    }

}