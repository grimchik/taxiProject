package rideservice.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import rideservice.dto.RideDTO;
import rideservice.dto.RideWithIdDTO;
import rideservice.dto.UpdateRideDTO;
import org.springframework.util.StringUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("test")
public class RideControllerSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;
    private String payload;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private Long savedRideId;

    @Given("a valid ride payload:")
    public void a_valid_ride_payload(String payload) {
        this.payload = payload;
    }

    @Given("an existing ride exists")
    public void an_existing_ride_exists() throws Exception {
        String ridePayload = "{" +
                "\"userId\": 1, " +
                "\"locations\": [" +
                "    {\"address\": \"123 Main St\", \"latitude\": \"37.774929\", \"longitude\": \"-122.419416\"}," +
                "    {\"address\": \"456 Second St\", \"latitude\": \"37.775000\", \"longitude\": \"-122.417000\"}" +
                "]" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(ridePayload, headers);
        ResponseEntity<RideWithIdDTO> postResponse = restTemplate.postForEntity("/api/v1/rides/", request, RideWithIdDTO.class);
        Assertions.assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        savedRideId = postResponse.getBody().getId();
        Assertions.assertNotNull(savedRideId);
    }

    @Given("a payload to update the ride:")
    public void a_payload_to_update_the_ride(String payload) {
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
        String resolvedUrl = url.contains("{id}") ? url.replace("{id}", savedRideId.toString()) : url;
        response = restTemplate.getForEntity(resolvedUrl, String.class);
    }

    @When("I send a PATCH request to {string}")
    public void i_send_a_patch_request_to(String url) {
        String resolvedUrl = url.contains("{id}") ? url.replace("{id}", savedRideId.toString()) : url;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.exchange(resolvedUrl, HttpMethod.PATCH, request, String.class);
    }

    @When("I send a DELETE request to {string}")
    public void i_send_a_delete_request_to(String url) {
        String resolvedUrl = url.contains("{id}") ? url.replace("{id}", savedRideId.toString()) : url;
        response = restTemplate.exchange(resolvedUrl, HttpMethod.DELETE, null, String.class);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer statusCode) {
        Assertions.assertEquals(statusCode.intValue(), response.getStatusCodeValue());
    }

    @Then("the response should contain the created ride with an id")
    public void the_response_should_contain_the_created_ride_with_an_id() throws Exception {
        RideWithIdDTO ride = objectMapper.readValue(response.getBody(), RideWithIdDTO.class);
        Assertions.assertNotNull(ride.getId());
    }

    @Then("the response should contain validation errors")
    public void the_response_should_contain_validation_errors() {
        Assertions.assertTrue(response.getBody().contains("must"));
    }

    @Then("the response should contain a ride with the same id")
    public void the_response_should_contain_a_ride_with_the_same_id() throws Exception {
        RideWithIdDTO ride = objectMapper.readValue(response.getBody(), RideWithIdDTO.class);
        Assertions.assertEquals(savedRideId, ride.getId());
    }

    @Then("the response should reflect the updated ride")
    public void the_response_should_reflect_the_updated_ride() throws Exception {
        // Предполагается, что при обновлении статус меняется на "UPDATED"
        RideWithIdDTO ride = objectMapper.readValue(response.getBody(), RideWithIdDTO.class);
        Assertions.assertEquals("UPDATED", ride.getStatus());
    }

    @Then("the response should contain a list of rides")
    public void the_response_should_contain_a_list_of_rides() {
        String body = response.getBody().trim();
        Assertions.assertTrue(body.startsWith("[") && body.endsWith("]"),
                "Response is not a JSON array");
    }

    @Then("the response should contain rides for user id {int}")
    public void the_response_should_contain_rides_for_user_id(Integer userId) throws Exception {
        RideWithIdDTO[] rides = objectMapper.readValue(response.getBody(), RideWithIdDTO[].class);
        for (RideWithIdDTO r : rides) {
            Assertions.assertEquals(userId.longValue(), r.getUserId());
        }
    }

    @Then("the response should contain rides for driver id {int}")
    public void the_response_should_contain_rides_for_driver_id(Integer driverId) throws Exception {
        RideWithIdDTO[] rides = objectMapper.readValue(response.getBody(), RideWithIdDTO[].class);
        for (RideWithIdDTO r : rides) {
            Assertions.assertEquals(driverId.longValue(), r.getDriverId());
        }
    }

    @Then("the response should contain available rides")
    public void the_response_should_contain_available_rides() {
        String body = response.getBody().trim();
        Assertions.assertTrue(body.startsWith("[") && body.endsWith("]"));
    }

    @Then("the response should contain completed rides for driver id {int}")
    public void the_response_should_contain_completed_rides_for_driver_id(Integer driverId) throws Exception {
        RideWithIdDTO[] rides = objectMapper.readValue(response.getBody(), RideWithIdDTO[].class);
        for (RideWithIdDTO r : rides) {
            Assertions.assertEquals(driverId.longValue(), r.getDriverId());
            Assertions.assertEquals("COMPLETED", r.getStatus());
        }
    }

    @Then("the response should contain an active ride for driver id {int}")
    public void the_response_should_contain_an_active_ride_for_driver_id(Integer driverId) throws Exception {
        RideWithIdDTO ride = objectMapper.readValue(response.getBody(), RideWithIdDTO.class);
        Assertions.assertEquals(driverId.longValue(), ride.getDriverId());
        Assertions.assertEquals("ACTIVE", ride.getStatus());
    }

    @Then("the response should reflect that the ride has been applied")
    public void the_response_should_reflect_that_the_ride_has_been_applied() throws Exception {
        RideWithIdDTO ride = objectMapper.readValue(response.getBody(), RideWithIdDTO.class);
        Assertions.assertNotNull(ride.getDriverId());
    }

    @Then("the response should contain completed rides within the period")
    public void the_response_should_contain_completed_rides_within_the_period() {
        String body = response.getBody().trim();
        Assertions.assertTrue(body.startsWith("[") && body.endsWith("]"));
    }

    @Then("the response should contain the total earnings for driver id {int}")
    public void the_response_should_contain_the_total_earnings_for_driver_id(Integer driverId) {
        try {
            Double earnings = Double.valueOf(response.getBody().trim());
            Assertions.assertTrue(earnings >= 0.0);
        } catch (NumberFormatException e) {
            Assertions.fail("The earnings is not a valid number");
        }
    }
}
