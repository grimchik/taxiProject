package driverservice.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import driverservice.dto.DriverWithIdDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("test")
public class DriverControllerSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;
    private String payload;
    private Long savedDriverId = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("the following driver details in JSON:")
    public void the_following_driver_details_in_JSON(String docString) {
        this.payload = docString;
    }

    @Given("the driver is created")
    public void the_driver_is_created() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<DriverWithIdDTO> createResponse = restTemplate.postForEntity("/api/v1/drivers/", request, DriverWithIdDTO.class);
    }

    @When("I send a POST request to {string}")
    public void i_send_a_post_request_to(String url) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }


    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String url) {
        response = restTemplate.getForEntity(resolveUrl(url), String.class);
    }

    @When("I send a PUT request to {string} with the following JSON:")
    public void i_send_a_put_request_to_with_the_following_JSON(String url, String docString) {
        this.payload = docString;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.exchange(resolveUrl(url), HttpMethod.PUT, request, String.class);
    }

    @When("I send a PATCH request to {string} with the following JSON:")
    public void i_send_a_patch_request_to_with_the_following_JSON(String url, String docString) {
        this.payload = docString;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.exchange(resolveUrl(url), HttpMethod.PATCH, request, String.class);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer statusCode) {
        Assertions.assertEquals(statusCode.intValue(), response.getStatusCodeValue());
    }

    @Then("the response should contain {string}: {string}")
    public void the_response_should_contain_field_value(String field, String expectedValue) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.getBody());
        String actualValue = jsonResponse.path(field).asText();
        Assertions.assertTrue(actualValue.equals(expectedValue),
                "Expected value for " + field + " is: " + expectedValue + ", but was: " + actualValue);
    }

    @When("I send a DELETE request to {string}")
    public void i_send_a_delete_request_to(String url) {
        response = restTemplate.exchange(resolveUrl(url), HttpMethod.DELETE, null, String.class);
    }

    @Then("the driver should be deleted")
    public void the_driver_should_be_deleted() {
        ResponseEntity<String> getResponse = restTemplate.getForEntity(resolveUrl("/api/v1/drivers/{driverId}"), String.class);
        Assertions.assertEquals(400, getResponse.getStatusCodeValue());
    }


    @Then("the response should contain a {string}")
    public void the_response_should_contain_a(String field) throws Exception {
        Assertions.assertTrue(response.getBody().contains(field));
    }

    private String resolveUrl(String url) {
        return url.contains("{driverId}") ? url.replace("{driverId}", savedDriverId.toString()) : url;
    }


}
