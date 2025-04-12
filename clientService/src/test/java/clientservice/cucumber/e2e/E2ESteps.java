package clientservice.cucumber.e2e;

import clientservice.client.*;
import clientservice.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("test")
public class E2ESteps
{
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RideServiceClient rideServiceClient;

    @MockBean
    private PromoCodeServiceClient promoCodeServiceClient;

    @MockBean
    private FeedbackServiceClient feedbackServiceClient;

    @MockBean
    private PaymentServiceClient paymentServiceClient;

    @MockBean
    private CarServiceClient carServiceClient;

    private PaymentWithIdDTO payment;
    private RideWithIdDTO updatedRide;
    private CheckPromoCodeDTO checkPromoCodeDTO;
    private ResponseEntity<String> response;
    private String payload;
    private Long savedUserId = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("the following user details in JSON:")
    public void the_following_user_details_in_JSON(String docString) {
        this.payload = docString;
    }

    @Given("the user is created")
    public void the_user_is_created() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<UserWithIdDTO> createResponse = restTemplate.postForEntity("/api/v1/users/", request, UserWithIdDTO.class);
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
        assertEquals(statusCode.intValue(), response.getStatusCodeValue());
    }

    @Then("the response should contain {string}")
    public void the_response_should_contain(String key) {
        String responseBody = response.getBody();
        assertTrue("Response should contain key: " + key, responseBody.contains(key));
    }

    @Given("the following ride details in JSON:")
    public void the_following_ride_details_in_JSON(String docString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(docString);
            ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).remove("userId");
            this.payload = objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process JSON", e);
        }
    }

    @Given("the ride is created")
    public void the_ride_is_created() throws Exception {
        JsonNode jsonNode = objectMapper.readTree(payload);
        JsonNode locationsNode = jsonNode.get("locations");

        RideWithIdDTO mockRideResponse = new RideWithIdDTO();
        mockRideResponse.setId(1L);
        mockRideResponse.setUserId(savedUserId);
        mockRideResponse.setStatus("REQUESTED");
        mockRideResponse.setPrice(11.0);
        mockRideResponse.setDriverId(null);
        mockRideResponse.setCarId(null);
        mockRideResponse.setLocations(objectMapper.treeToValue(locationsNode, List.class));

        updatedRide = mockRideResponse;

        when(rideServiceClient.createRide(any()))
                .thenReturn(mockRideResponse);
    }

    @When("I send a POST request to ride {string}")
    public void i_send_a_post_request_to_ride_service(String url) throws JsonProcessingException {
        url = resolveUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @Given("a valid promo code payload:")
    public void a_valid_promo_code_payload(String docString) {
        this.payload = docString;
    }

    @Given("the promo code is created")
    public void the_promo_code_is_created() throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);

        PromoCodeWithIdDTO mockPromoResponse = new PromoCodeWithIdDTO();
        mockPromoResponse.setId(1L);
        mockPromoResponse.setPercent(jsonNode.get("percent").asLong());
        mockPromoResponse.setActivationDate(LocalDate.parse(jsonNode.get("activationDate").asText()));
        mockPromoResponse.setKeyword(jsonNode.get("keyword").asText());
        mockPromoResponse.setExpiryDate(LocalDate.parse(jsonNode.get("expiryDate").asText()));

        when(promoCodeServiceClient.createPromoCode(any()))
                .thenReturn(mockPromoResponse);
    }

    @Given("a valid car payload:")
    public void a_valid_car_payload(String docString) {
        this.payload = docString;
    }

    @Given("the car is created")
    public void the_car_is_created() throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);

        CarWithIdDTO mockCarResponse = new CarWithIdDTO();
        mockCarResponse.setId(1L);
        mockCarResponse.setBrand(jsonNode.get("brand").asText());
        mockCarResponse.setModel(jsonNode.get("model").asText());
        mockCarResponse.setDescription(jsonNode.get("description").asText());
        mockCarResponse.setColor(jsonNode.get("color").asText());
        mockCarResponse.setCategory(jsonNode.get("category").asText());
        mockCarResponse.setNumber(jsonNode.get("number").asText());

        when(carServiceClient.createCar(any()))
                .thenReturn(mockCarResponse);
    }

    @When("I send a POST request to car {string}")
    public void i_send_a_post_request_to_car_service(String url) throws JsonProcessingException {
        url = resolveUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @Then("the response should contain the created car with an id")
    public void the_response_should_contain_the_created_car_with_an_id() {
        String responseBody = response.getBody();
        assertTrue("Response should contain car id", responseBody.contains("id"));
    }

    @Given("a valid feedback payload:")
    public void a_valid_feedback_payload(String docString) {
        this.payload = docString;
    }

    @Given("the feedback is created")
    public void the_feedback_is_created() throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(payload);

        ClientFeedbackWithIdDTO mockClientFeedback = new ClientFeedbackWithIdDTO();
        mockClientFeedback.setId(1L);
        mockClientFeedback.setRate(jsonNode.get("rate").asLong());
        mockClientFeedback.setComment(jsonNode.get("comment").asText());
        mockClientFeedback.setCleanInterior(jsonNode.get("cleanInterior").asBoolean());
        mockClientFeedback.setSafeDriving(jsonNode.get("safeDriving").asBoolean());
        mockClientFeedback.setNiceMusic(jsonNode.get("niceMusic").asBoolean());
        mockClientFeedback.setUserId(jsonNode.get("userId").asLong());
        mockClientFeedback.setRideId(jsonNode.get("rideId").asLong());

        when(feedbackServiceClient.createClientFeedback(any()))
                .thenReturn(mockClientFeedback);
    }

    @When("I send a POST request to feedback {string}")
    public void i_send_a_post_request_to_feedback_service(String url) throws JsonProcessingException {
        url = resolveUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @Then("the response should contain the created feedback with an id")
    public void the_response_should_contain_the_created_feedback_with_an_id() {
        String responseBody = response.getBody();
        assertTrue("Response should contain car id", responseBody.contains("id"));
    }

    @Given("a valid payment payload:")
    public void a_valid_payment_payload(String docString) throws JsonProcessingException {
        this.payment = new PaymentWithIdDTO();
        this.payload = docString;

        JsonNode jsonNode = objectMapper.readTree(payload);

        payment.setPaymentDate(LocalDateTime.now());
        payment.setId(1L);
        payment.setPaymentType("CARD");
        payment.setPrice(11.0);
        payment.setCardNumber("1111-1111-1111-1111");
        when(paymentServiceClient.confirmedPayment(any(), any(), any()))
                .thenReturn(payment);
    }

    @When("I send a PATCH request to {string}")
    public void i_send_a_patch_request_to_service(String url,String requestBody) throws JsonProcessingException {
        url = url.replace("{userId}", String.valueOf(1))
                .replace("{paymentId}", String.valueOf(1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        response = restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);
    }

    @Then("the payment should be paid")
    public void the_payment_should_be_paid() {
        String responseBody = response.getBody();
        Assertions.assertNotNull(responseBody, "Response body should not be null");
        Assertions.assertTrue(responseBody.contains("CARD"), "Response should contain 'CARD' payment Type");
    }

    private String resolveUrl(String url) {
        return url.contains("{userId}") ? url.replace("{userId}", savedUserId.toString()) : url;
    }

}
