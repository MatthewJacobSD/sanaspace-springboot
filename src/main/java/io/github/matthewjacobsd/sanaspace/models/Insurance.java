package io.github.matthewjacobsd.sanaspace.models;

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

    // phone_number (min 10, max 12)
    @Size(min = 10, max = 12, message = "Phone number must be up to 12 digits")
    @Pattern(
        message = "Invalid phone number",
        regexp =  "^\\+?[0-9]{3}-[0-9]{3}-[0-9]{4}$"
    )
    @Column(name = "phone_number")
    private String phoneNumber;

    // relationships

    // One-to-Many relationship with Patient
    @Builder.Default
    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Patient> patients = new ArrayList<>();

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
            } else {
                System.err.println("Invalid phone number format: " + digits);
            }
        }    
    }

}
