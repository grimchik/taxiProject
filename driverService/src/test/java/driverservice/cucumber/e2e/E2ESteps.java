package driverservice.cucumber.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import driverservice.client.CarServiceClient;
import driverservice.client.DriverFeedbackServiceClient;
import driverservice.client.RideServiceClient;
import driverservice.dto.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("test")
public class E2ESteps {

    @MockBean
    private DriverFeedbackServiceClient driverFeedbackServiceClient;

    @MockBean
    private CarServiceClient carServiceClient;

    @MockBean
    private RideServiceClient rideServiceClient;

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
    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer statusCode) {
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
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

    @Then("the response should contain a {string}")
    public void the_response_should_contain_a(String field) throws Exception {
        Assertions.assertTrue(response.getBody().contains(field));
    }

    @Given("a valid feedback payload:")
    public void a_valid_feedback_payload(String docString) {
        this.payload = docString;
    }

    @Given("the feedback is created")
    public void the_feedback_is_created() throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);

        DriverFeedbackWithIdDTO mockDriverFeedback = new DriverFeedbackWithIdDTO();
        mockDriverFeedback.setId(1L);
        mockDriverFeedback.setRate(jsonNode.get("rate").asLong());
        mockDriverFeedback.setComment(jsonNode.get("comment").asText());
        mockDriverFeedback.setPolitePassenger(jsonNode.get("politePassenger").asBoolean());
        mockDriverFeedback.setCleanPassenger(jsonNode.get("cleanPassenger").asBoolean());
        mockDriverFeedback.setPunctuality(jsonNode.get("punctuality").asBoolean());
        mockDriverFeedback.setDriverId(jsonNode.get("driverId").asLong());
        mockDriverFeedback.setRideId(jsonNode.get("rideId").asLong());

        when(driverFeedbackServiceClient.createDriverFeedback(any()))
                .thenReturn(mockDriverFeedback);
    }

    @When("I send a POST request to feedback {string}")
    public void i_send_a_post_request_to_feedback_service(String url) throws JsonProcessingException {
        url = resolveUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @Given("the car is created:")
    public void the_car_is_created(String docString) throws JsonProcessingException {
        this.payload = docString;
        JsonNode jsonNode = objectMapper.readTree(payload);

        CarWithIdDTO mockCarResponse = new CarWithIdDTO();
        mockCarResponse.setId(1L);
        mockCarResponse.setBrand(jsonNode.get("brand").asText());
        mockCarResponse.setModel(jsonNode.get("model").asText());
        mockCarResponse.setDescription(jsonNode.get("description").asText());
        mockCarResponse.setColor(jsonNode.get("color").asText());
        mockCarResponse.setCategory(jsonNode.get("category").asText());
        mockCarResponse.setNumber(jsonNode.get("number").asText());

        when(carServiceClient.getCarById(any()))
                .thenReturn(mockCarResponse);
        RideWithIdDTO mockRideResponse = new RideWithIdDTO();
        mockRideResponse.setId(null);

        when(rideServiceClient.getActiveRide(any())).thenReturn(mockRideResponse);
        RateDTO mockRateDTO = new RateDTO();
        mockRateDTO.setRate(4.5);
        when(driverFeedbackServiceClient.getDriverRate(any())).thenReturn(mockRateDTO);
    }

    @Then("the response should contain the created feedback with an id")
    public void the_response_should_contain_the_created_feedback_with_an_id() {
        String responseBody = response.getBody();
        assertTrue("Response should contain car id", responseBody.contains("id"));
    }

    @When("I send a POST request to assign car {string}")
    public void i_send_a_post_request_to_assign_car(String url) {
        url = resolveUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @Given("the ride is created:")
    public void the_ride_is_created(String docString) throws JsonProcessingException {
        this.payload = docString;
        JsonNode jsonNode = objectMapper.readTree(payload);
        JsonNode locationsNode = jsonNode.get("locations");

        RideWithIdDTO mockRideResponse = new RideWithIdDTO();
        mockRideResponse.setId(1L);
        mockRideResponse.setUserId(1L);
        mockRideResponse.setStatus("REQUESTED");
        mockRideResponse.setPrice(11.0);
        mockRideResponse.setDriverId(1L);
        mockRideResponse.setCarId(1L);
        mockRideResponse.setLocations(objectMapper.treeToValue(locationsNode, List.class));

        when(rideServiceClient.applyRide(any(),any()))
                .thenReturn(mockRideResponse);
    }

    @When("I send a POST request to apply for a ride {string}")
    public void i_send_a_post_request_to_apply_ride(String url) {
        url = resolveUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    private String resolveUrl(String url) {
        return url.contains("{driverId}") ? url.replace("{driverId}", savedDriverId.toString()) : url;
    }

}
