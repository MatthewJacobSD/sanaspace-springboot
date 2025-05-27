package io.github.matthewjacobsd.sanaspace.models.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

// Composite key for Visit
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VisitId  implements Serializable {
    
    // patient_id
    @Column(name = "patient_id")
    private String patientId;

    // doctor_id
    @Column(name = "doctor_id")
    private String doctorId;

    // visit_date
    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitId visitId = (VisitId) o;
        return Objects.equals(patientId, visitId.patientId) &&
               Objects.equals(doctorId, visitId.doctorId) &&
               Objects.equals(visitDate, visitId.visitDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, doctorId, visitDate);
    }
}