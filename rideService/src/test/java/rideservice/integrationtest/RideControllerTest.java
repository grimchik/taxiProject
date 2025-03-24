package rideservice.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import rideservice.dto.CarAndDriverIdDTO;
import rideservice.dto.LocationDTO;
import rideservice.dto.RideDTO;
import rideservice.dto.UpdateRideDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAndGetRide() throws Exception {
        RideDTO rideDTO = new RideDTO(6L, List.of(
                new LocationDTO("Start Address", "40.712776", "-74.005974"),
                new LocationDTO("End Address", "40.73061", "-73.935242")
        ));

        String createResponse = mockMvc.perform(
                        post("/api/v1/rides/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rideDTO))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long rideId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/api/v1/rides/{id}", rideId))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateRidePatch() throws Exception {
        RideDTO rideDTO = new RideDTO(1L, List.of(
                new LocationDTO("Start Address", "40.712776", "-74.005974"),
                new LocationDTO("End Address", "40.73061", "-73.935242")
        ));

        String createResponse = mockMvc.perform(
                        post("/api/v1/rides/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rideDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long rideId = objectMapper.readTree(createResponse).get("id").asLong();
        UpdateRideDTO updateRideDTO = new UpdateRideDTO(1L, List.of(
                new LocationDTO("Updated Start Address", "40.712776", "-74.005974"),
                new LocationDTO("Updated End Address", "40.73061", "-73.935242")
        ));

        mockMvc.perform(
                        patch("/api/v1/rides/{id}", rideId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRideDTO))
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRide() throws Exception {
        RideDTO rideDTO = new RideDTO(5L, List.of(
                new LocationDTO("Start Address", "40.712776", "-74.005974"),
                new LocationDTO("End Address", "40.73061", "-73.935242")
        ));

        String createResponse = mockMvc.perform(
                        post("/api/v1/rides/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rideDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long rideId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1/rides/{id}", rideId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/rides/{id}", rideId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllRides() throws Exception {
        mockMvc.perform(get("/api/v1/rides/?page=0&size=5"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllRidesByUserId() throws Exception {
        mockMvc.perform(get("/api/v1/rides/user-rides/{userId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllRidesByDriverId() throws Exception {
        mockMvc.perform(get("/api/v1/rides/driver-rides/{driverId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetActiveRide() throws Exception {
        mockMvc.perform(get("/api/v1/rides/active-ride/{driverId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCompletedRides() throws Exception {
        mockMvc.perform(get("/api/v1/rides/completed-rides/{driverId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCompletedRidesPeriod() throws Exception {
        mockMvc.perform(get("/api/v1/rides/completed-rides-period/{driverId}?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEarnings() throws Exception {
        mockMvc.perform(get("/api/v1/rides/earning/{driverId}?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59", 1))
                .andExpect(status().isOk());
    }

}
