package io.github.matthewjacobsd.sanaspace.models;

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
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    // lastname
    @Column(name = "last_name")
    private String lastName;

    // postcode
    @Column(name = "postcode")
    private String postcode;

    // address
    @Column(name = "address")
    private String address;

    // phone_number (max 12)
    @Size(max = 12, message = "Phone number must be up to 12 digits")
    @Pattern(
        message = "Invalid phone number",
        regexp =  "^\\+?[0-9]{3}-[0-9]{3}-[0-9]{4}$"
    )
    @Column(name = "phone_number")
    private String phoneNumber;

    // email
    @NotNull
    @Email(message = "Invalid email address")
    @Column(name = "email", nullable = false)
    private String email;

    // Highlight insurerance if patient has one
    @Column(name = "patient_insurance")
    private boolean isInsured = false;

    // relationships

    // One-to-Many relationship with Prescription
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Prescription> prescriptions = new ArrayList<>();

    // One-to-Many relationship with Visit
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Visit> visits = new ArrayList<>();

    // Many-to-One relationship with Insurance
    @ManyToOne
    @JoinColumn(name = "insurance_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Insurance insurance;

    // auto-refresh insurnace status
    @PrePersist
    @PreUpdate
    private void updateInsuranceStatus() {
        this.isInsured = insurance != null;
    }

    // auto-update phone pattern
    private void updatePhoneNumber() {
        if (phoneNumber != null) {
            // Remove all non-digit characters
            String digits = phoneNumber.replaceAll("[^0-9]", "");
            // Check if we have at least 10 digits
            if (digits.length() >= 10) {
                // Format as +XXX-XXX-XXXX (take first 10 digits)
                digits = digits.substring(0, 10);
                phoneNumber = String.format("+%s-%s-%s", 
                    digits.substring(0, 3), 
                    digits.substring(3, 6), 
                    digits.substring(6, 10));
            }
        }    
    }
}
