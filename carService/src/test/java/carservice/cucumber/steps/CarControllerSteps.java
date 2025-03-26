package carservice.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import carservice.dto.CarDTO;
import carservice.dto.CarWithIdDTO;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("test")
public class CarControllerSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;
    private String payload;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Long savedCarId;

    @Given("a valid car payload:")
    public void a_valid_car_payload(String payload) {
        this.payload = payload;
    }

    @Given("a car payload with invalid number format:")
    public void a_car_payload_with_invalid_number_format(String payload) {
        this.payload = payload;
    }

    @Given("an existing car exists")
    public void an_existing_car_exists() throws Exception {
        int randomFourDigits = ThreadLocalRandom.current().nextInt(1000, 10000);
        String uniqueNumber = randomFourDigits + "AB-3";
        CarDTO carDTO = new CarDTO("Toyota", "Camry", "Initial description", "Red", "ECONOMY", uniqueNumber);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CarDTO> request = new HttpEntity<>(carDTO, headers);
        ResponseEntity<CarWithIdDTO> postResponse = restTemplate.postForEntity("/api/v1/cars/", request, CarWithIdDTO.class);
        Assertions.assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        savedCarId = postResponse.getBody().getId();
        Assertions.assertNotNull(savedCarId);
    }

    private String resolveUrl(String url) {
        return url.contains("{id}") ? url.replace("{id}", savedCarId.toString()) : url;
    }

    @Given("a payload to update the car:")
    public void a_payload_to_update_the_car(String payload) {
        this.payload = payload;
    }

    @Given("a payload to partially update the car:")
    public void a_payload_to_partially_update_the_car(String payload) {
        this.payload = payload;
    }

    @When("I send a POST request to {string}")
    public void i_send_a_post_request_to(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String url) {
        String resolvedUrl = resolveUrl(url);
        response = restTemplate.getForEntity(resolvedUrl, String.class);
    }

    @When("I send a PUT request to {string}")
    public void i_send_a_put_request_to(String url) {
        String resolvedUrl = resolveUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.exchange(resolvedUrl, HttpMethod.PUT, request, String.class);
    }

    @When("I send a PATCH request to {string}")
    public void i_send_a_patch_request_to(String url) {
        String resolvedUrl = resolveUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.exchange(resolvedUrl, HttpMethod.PATCH, request, String.class);
    }

    @When("I send a DELETE request to {string}")
    public void i_send_a_delete_request_to(String url) {
        String resolvedUrl = resolveUrl(url);
        response = restTemplate.exchange(resolvedUrl, HttpMethod.DELETE, null, String.class);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer statusCode) {
        Assertions.assertEquals(statusCode.intValue(), response.getStatusCodeValue());
    }

    @Then("the response should contain the created car with an id")
    public void the_response_should_contain_the_created_car_with_an_id() throws Exception {
        CarWithIdDTO car = objectMapper.readValue(response.getBody(), CarWithIdDTO.class);
        Assertions.assertNotNull(car.getId());
        Assertions.assertEquals("Toyota", car.getBrand());
    }

    @Then("the response should contain validation errors")
    public void the_response_should_contain_validation_errors() {
        Assertions.assertTrue(response.getBody().contains("Number must be in format"));
    }

    @Then("the response should contain a car with the same id")
    public void the_response_should_contain_a_car_with_the_same_id() throws Exception {
        CarWithIdDTO car = objectMapper.readValue(response.getBody(), CarWithIdDTO.class);
        Assertions.assertEquals(savedCarId, car.getId());
    }

    @Then("the response should reflect the updated car")
    public void the_response_should_reflect_the_updated_car() throws Exception {
        CarWithIdDTO car = objectMapper.readValue(response.getBody(), CarWithIdDTO.class);
        Assertions.assertEquals("Honda", car.getBrand());
        Assertions.assertEquals("Accord", car.getModel());
    }

    @Then("the response should reflect the updated color")
    public void the_response_should_reflect_the_updated_color() throws Exception {
        CarWithIdDTO car = objectMapper.readValue(response.getBody(), CarWithIdDTO.class);
        Assertions.assertEquals("Yellow", car.getColor());
    }
}
