package io.github.matthewjacobsd.sanaspace;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.matthewjacobsd.sanaspace.models.Doctor;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllDoctors() throws Exception {
        mockMvc.perform(get("/api/doctors?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetDoctorById() throws Exception {
        String id = "ff7c0432-89ab-40c5-bc50-f6b77439d9c6";
        mockMvc.perform(get("/api/doctors/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateDoctor() throws Exception {
        Doctor doctor = Doctor.builder()
                .firstName("Amara")
                .lastName("Patel")
                .address("245 Oakwood Lane, Boston, MA 02108")
                .email("amara.patel@healthcare.org")
                .specialization(Doctor.Specialization.Cardiology)
                .experience(Doctor.Experience.Senior)
                .build();

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("amara.patel@healthcare.org"));
    }

    @Test
    void testUpdateDoctor() throws Exception {
        String id = "ff7c0432-89ab-40c5-bc50-f6b77439d9c6";
        Doctor doctor = Doctor.builder()
                .firstName("Michael")
                .lastName("Smith")
                .address("30 Joe Doe Street, Chicago, IL 60601")
                .email("michael.smith@clinic.net")
                .specialization(Doctor.Specialization.Oncologists)
                .experience(Doctor.Experience.Senior)
                .build();

        mockMvc.perform(put("/api/doctors/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("michael.smith@clinic.net"));
    }

    @Test
    void testPatchDoctor() throws Exception {
        String id = "ff7c0432-89ab-40c5-bc50-f6b77439d9c6";
        String patchRequest = "{\"firstName\": \"William\", \"email\": \"william.hello@healthcare.org\"}";

        mockMvc.perform(patch("/api/doctors/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("William"));
    }

    @Test
    void testDeleteDoctor() throws Exception {
        String id = "ff7c0432-89ab-40c5-bc50-f6b77439d9c6";
        mockMvc.perform(delete("/api/doctors/" + id))
                .andExpect(status().isNoContent());
    }
}