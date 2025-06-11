package io.github.matthewjacobsd.sanaspace.services;

import io.github.matthewjacobsd.sanaspace.exceptions.ExpInsurance;
import io.github.matthewjacobsd.sanaspace.models.Insurance;
import io.github.matthewjacobsd.sanaspace.repositories.RInsurance;
import io.github.matthewjacobsd.sanaspace.utils.GlobalExceptionHandler.SupplierWithException;
import io.github.matthewjacobsd.sanaspace.utils.LoggerUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class SInsurance {

    private final LoggerUtil logger = new LoggerUtil(SInsurance.class);
    private final RInsurance insuranceR;

    // Helper method to handle operations and exceptions
    private <T> T handleOperation(String operation, long startTime, SupplierWithException<T> supplier) {
        try {
            T result = supplier.get();
            logger.success("✅ " + operation, startTime);
            return result;
        } catch (ExpInsurance e) {
            logger.error("❌ " + e.getMessage(), startTime, e, operation);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("❌ Invalid input for " + operation, startTime, e, operation);
            throw new IllegalArgumentException("Invalid data: " + e.getMessage() + "; errorCode=INVALID_INPUT");
        } catch (Exception e) {
            logger.error("❌ Failed " + operation, startTime, e, operation);
            throw new RuntimeException("Failed " + operation + ": " + e.getMessage() + "; errorCode=SERVER_ERROR");
        }
    }

    // Saves a new Insurance entity
    public Insurance saveInsurance(@NotNull Insurance i) {
        long startTime = logger.startOperation("Saving insurance...", Map.of("companyName", i.getCompanyName()));
        return handleOperation("save insurance", startTime, () -> insuranceR.save(i));
    }

    // Retrieves all Insurances with pagination
    public Page<Insurance> getPagedInsurances(Pageable pageable) {
        long startTime = logger.startOperation("Fetching insurances...", Map.of("page", pageable.getPageNumber(), "size", pageable.getPageSize()));
        return handleOperation("fetch all insurances", startTime, () -> insuranceR.findAll(pageable));
    }

    // Retrieves all Insurances with pagination (legacy method)
    public Page<Insurance> fetchAllInsurances(int page, int size) {
        long startTime = logger.startOperation("Fetching insurances...", Map.of("page", page, "size", size));
        return handleOperation("fetch all insurances", startTime, () ->
                insuranceR.findAll(PageRequest.of(page, size)));
    }

    // Retrieves an Insurance by ID
    public Insurance fetchInsuranceById(String id) {
        long startTime = logger.startOperation("Fetching single insurance...", Map.of("id", id));
        return handleOperation("fetch insurance by ID", startTime, () ->
                insuranceR.findById(id).orElseThrow(() -> new ExpInsurance(id)));
    }

    // Updates an Insurance by ID
    public Insurance updateInsurance(String id, Insurance i) {
        long startTime = logger.startOperation("Updating insurance...", Map.of("id", id, "companyName", i.getCompanyName()));
        return handleOperation("update insurance", startTime, () -> {
            Insurance existingInsurance = insuranceR.findById(id).orElseThrow(() -> new ExpInsurance(id));
            existingInsurance.setCompanyName(i.getCompanyName());
            existingInsurance.setAddress(i.getAddress());
            existingInsurance.setPhoneNumber(i.getPhoneNumber());
            return insuranceR.save(existingInsurance);
        });
    }

    // Partial update of an Insurance by ID
    public Insurance updateInsuranceFields(@NotNull String id, @NotNull Map<String, Object> updates) {
        long startTime = logger.startOperation("Updating insurance fields...", Map.of("id", id, "updates", updates.keySet()));
        return handleOperation("partial update insurance", startTime, () -> {
            Insurance insurance = insuranceR.findById(id).orElseThrow(() -> new ExpInsurance(id));
            updates.forEach((field, value) -> {
                switch (field) {
                    case "companyName" -> insurance.setCompanyName(value != null ? (String) value : null);
                    case "address" -> insurance.setAddress(value != null ? (String) value : null);
                    case "phoneNumber" -> insurance.setPhoneNumber(value != null ? (String) value : null);
                    default -> throw new IllegalArgumentException("Invalid field: " + field);
                }
            });
            return insuranceR.save(insurance);
        });
    }

    // Deletes an Insurance by ID
    public void deleteInsurance(String id) {
        long startTime = logger.startOperation("Deleting insurance...", Map.of("id", id));
        handleOperation("delete insurance", startTime, () -> {
            Insurance insurance = insuranceR.findById(id).orElseThrow(() -> new ExpInsurance(id));
            insuranceR.delete(insurance);
            return null; 
        });
    }
}