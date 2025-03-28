package driverservice.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import driverservice.dto.DriverDTO;
import driverservice.dto.UpdateDriverDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAndGetDriver() throws Exception {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setUsername("Oleg1");
        driverDTO.setName("Oleg");
        driverDTO.setPhone("+375333565910");
        driverDTO.setPassword("password123");

        String createResponse = mockMvc.perform(
                        post("/api/v1/drivers/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(driverDTO))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Oleg1"))
                .andExpect(jsonPath("$.name").value("Oleg"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long driverId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1/drivers/{id}", driverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Oleg1"))
                .andExpect(jsonPath("$.name").value("Oleg"));
    }

    @Test
    public void testUpdateDriverPatch() throws Exception {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setUsername("Oleg2");
        driverDTO.setName("Oleg");
        driverDTO.setPhone("+375333565914");
        driverDTO.setPassword("password123");

        String createResponse = mockMvc.perform(
                        post("/api/v1/drivers/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(driverDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long driverId = objectMapper.readTree(createResponse).get("id").asLong();
        UpdateDriverDTO updateDriverDTO = new UpdateDriverDTO();
        updateDriverDTO.setName("Olezha");

        mockMvc.perform(
                        patch("/api/v1/drivers/{id}", driverId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDriverDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Olezha"));
    }

    @Test
    public void testDeleteDriver() throws Exception {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setUsername("Oleg3");
        driverDTO.setName("Oleg");
        driverDTO.setPhone("+375333565915");
        driverDTO.setPassword("password123");

        String createResponse = mockMvc.perform(
                        post("/api/v1/drivers/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(driverDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long driverId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1/drivers/{id}", driverId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/drivers/{id}", driverId))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateDriverPut() throws Exception {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setUsername("Oleg5");
        driverDTO.setName("Oleg");
        driverDTO.setPhone("+375333565911");
        driverDTO.setPassword("password123");

        String createResponse = mockMvc.perform(
                        post("/api/v1/drivers/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(driverDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long driverId = objectMapper.readTree(createResponse).get("id").asLong();
        driverDTO.setUsername("Oleg5");
        driverDTO.setName("Olezha");
        driverDTO.setPhone("+375333565911");
        driverDTO.setPassword("password12=3");

        mockMvc.perform(
                        put("/api/v1/drivers/{id}", driverId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(driverDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Olezha"));
    }

    @Test
    public void testUpdateDriverSamePassword() throws Exception {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setUsername("Oleg6");
        driverDTO.setName("Oleg");
        driverDTO.setPhone("+375333565918");
        driverDTO.setPassword("password123");

        String createResponse = mockMvc.perform(
                        post("/api/v1/drivers/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(driverDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long driverId = objectMapper.readTree(createResponse).get("id").asLong();
        driverDTO.setUsername("Oleg6");
        driverDTO.setName("Olezha");
        driverDTO.setPhone("+375333565918");
        driverDTO.setPassword("password123");

        mockMvc.perform(
                        put("/api/v1/drivers/{id}", driverId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(driverDTO))
                )
                .andExpect(status().isBadRequest());
    }
}