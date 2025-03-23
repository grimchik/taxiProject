package carservice.integrationtest;

import carservice.dto.CarDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testCreateCar() throws Exception {
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Toyota");
        carDTO.setModel("Camry");
        carDTO.setDescription("Sedan car");
        carDTO.setColor("White");
        carDTO.setCategory("ECONOMY");
        carDTO.setNumber("1234AB-3");

        String response = mockMvc.perform(post("/api/v1/cars/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        Long id = jsonNode.get("id").asLong();
        assertThat(id).isNotNull();
    }

    @Test
    public void testGetCarById() throws Exception {
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Honda");
        carDTO.setModel("Civic");
        carDTO.setDescription("Compact car");
        carDTO.setColor("Black");
        carDTO.setCategory("COMFORT");
        carDTO.setNumber("5678CD-5");

        String response = mockMvc.perform(post("/api/v1/cars/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/v1/cars/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Civic"));
    }

    @Test
    public void testUpdateCar() throws Exception {
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Ford");
        carDTO.setModel("Focus");
        carDTO.setDescription("Hatchback");
        carDTO.setColor("Blue");
        carDTO.setCategory("ECONOMY");
        carDTO.setNumber("9012EF-4");

        String response = mockMvc.perform(post("/api/v1/cars/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        CarDTO updateDTO = new CarDTO();
        updateDTO.setBrand("Ford");
        updateDTO.setModel("Fusion");
        updateDTO.setDescription("Sedan");
        updateDTO.setColor("Red");
        updateDTO.setCategory("BUSINESS");
        updateDTO.setNumber("9012EF-4");

        mockMvc.perform(put("/api/v1/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Fusion"))
                .andExpect(jsonPath("$.category").value("BUSINESS"));
    }

    @Test
    public void testDeleteCar() throws Exception {
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("BMW");
        carDTO.setModel("X3");
        carDTO.setDescription("SUV");
        carDTO.setColor("Silver");
        carDTO.setCategory("BUSINESS");
        carDTO.setNumber("3456GH-2");

        String response = mockMvc.perform(post("/api/v1/cars/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/cars/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/cars/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllCars() throws Exception {
        CarDTO car1 = new CarDTO();
        car1.setBrand("Audi");
        car1.setModel("A4");
        car1.setDescription("Luxury sedan");
        car1.setColor("Grey");
        car1.setCategory("COMFORT");
        car1.setNumber("7890IJ-6");

        CarDTO car2 = new CarDTO();
        car2.setBrand("Mercedes");
        car2.setModel("C200");
        car2.setDescription("Executive");
        car2.setColor("Black");
        car2.setCategory("BUSINESS");
        car2.setNumber("2345KL-7");

        mockMvc.perform(post("/api/v1/cars/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/cars/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/cars/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(Matchers.greaterThanOrEqualTo(2))));
    }
}