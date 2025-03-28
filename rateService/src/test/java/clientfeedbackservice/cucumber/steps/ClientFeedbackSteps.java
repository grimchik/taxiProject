package clientfeedbackservice.cucumber.steps;

import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import rateservice.RateServiceApplication;
import rateservice.client.RideServiceClient;
import rateservice.dto.RideWithIdDTO;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = RateServiceApplication.class)
@CucumberContextConfiguration
@ActiveProfiles("test")
public class ClientFeedbackSteps {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RideServiceClient rideServiceClient;

    private ResponseEntity<String> response;
    private String payload;
    private Long savedFeedbackId = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("the following client feedback details in JSON:")
    public void the_following_client_feedback_details_in_JSON(String docString) {
        this.payload = docString;
    }

    @Given("the ride with ID {long} is completed")
    public void the_ride_is_completed(Long rideId) {
        RideWithIdDTO ride = new RideWithIdDTO();
        ride.setId(rideId);
        ride.setUserId(123L);
        ride.setStatus("COMPLETED");

        when(rideServiceClient.getRide(rideId)).thenReturn(ride);
    }

    @Given("a client feedback is created")
    public void a_client_feedback_is_created() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/v1/client-feedbacks/", request, String.class);

        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        try {
            JsonNode jsonNode = objectMapper.readTree(createResponse.getBody());
            savedFeedbackId = jsonNode.get("id").asLong();
        } catch (Exception e) {
            Assertions.fail("Failed to parse response");
        }
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

    @When("I send a DELETE request to {string}")
    public void i_send_a_delete_request_to(String url) {
        response = restTemplate.exchange(resolveUrl(url), HttpMethod.DELETE, null, String.class);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer statusCode) {
        Assertions.assertEquals(statusCode.intValue(), response.getStatusCodeValue());
    }

    @Then("the response should contain {string}")
    public void the_response_should_contain(String field) throws Exception {
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        Assertions.assertNotNull(jsonResponse.get(field), "Field " + field + " should be present in the response");
    }


    @Then("the response should contain {string}: {string}")
    public void the_response_should_contain_field_value(String field, String expectedValue) throws Exception {
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        Assertions.assertEquals(expectedValue, jsonResponse.path(field).asText(), "Expected value for " + field);
    }

    @Then("the response should contain {string}: {int}")
    public void the_response_should_contain_field_value_int(String field, Integer expectedValue) throws Exception {
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        Assertions.assertEquals(expectedValue.intValue(), jsonResponse.path(field).asInt(), "Expected value for " + field);
    }

    @Then("the feedback should be deleted")
    public void the_feedback_should_be_deleted() {
        ResponseEntity<String> getResponse = restTemplate.getForEntity(resolveUrl("/api/v1/client-feedbacks/{feedbackId}"), String.class);
        Assertions.assertEquals(404, getResponse.getStatusCodeValue());
    }

    private String resolveUrl(String url) {
        return url.contains("{feedbackId}") ? url.replace("{feedbackId}", savedFeedbackId.toString()) : url;
    }
}
