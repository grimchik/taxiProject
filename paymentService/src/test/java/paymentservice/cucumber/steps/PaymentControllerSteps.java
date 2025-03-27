package paymentservice.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import paymentservice.dto.PaymentDTO;
import paymentservice.dto.PaymentWithIdDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("test")
public class PaymentControllerSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;
    private String payload;
    private Long savedPaymentId;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Given("a valid payment payload:")
    public void a_valid_payment_payload(String payload) {
        this.payload = payload;
    }
    @Given("a payload to update the payment:")
    public void a_payload_to_update_the_payment(String docString) {
        this.payload = docString;
    }

    @Given("a payload to partially update the payment:")
    public void a_payload_to_partially_update_the_payment(String docString) {
        this.payload = docString;
    }

    @Then("the response should reflect the updated payment with new price")
    public void the_response_should_reflect_the_updated_payment_with_new_price() throws Exception {
        PaymentWithIdDTO payment = objectMapper.readValue(response.getBody(), PaymentWithIdDTO.class);
        Assertions.assertEquals(150.75, payment.getPrice());
    }

    @Given("a payment payload with invalid data:")
    public void a_payment_payload_with_invalid_data(String payload) {
        this.payload = payload;
    }

    @Given("an existing payment exists")
    public void an_existing_payment_exists() throws Exception {
        PaymentDTO paymentDTO = new PaymentDTO(100.0, "CARD", "1234-5678-9101-1121",1L,42L);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentDTO> request = new HttpEntity<>(paymentDTO, headers);
        ResponseEntity<PaymentWithIdDTO> postResponse = restTemplate.postForEntity("/api/v1/payments/", request, PaymentWithIdDTO.class);
        Assertions.assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        savedPaymentId = postResponse.getBody().getId();
        Assertions.assertNotNull(savedPaymentId);
    }


    private String resolveUrl(String url) {
        return url.contains("{id}") ? url.replace("{id}", savedPaymentId.toString()) : url;
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

    @When("I send a PUT request to {string}")
    public void i_send_a_put_request_to(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        response = restTemplate.exchange(resolveUrl(url), HttpMethod.PUT, request, String.class);
    }

    @When("I send a PATCH request to {string}")
    public void i_send_a_patch_request_to(String url) {
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

    @Then("the response should contain the created payment with an id")
    public void the_response_should_contain_the_created_payment_with_an_id() throws Exception {
        PaymentWithIdDTO payment = objectMapper.readValue(response.getBody(), PaymentWithIdDTO.class);
        Assertions.assertNotNull(payment.getId());
    }

    @Then("the response should contain validation errors")
    public void the_response_should_contain_validation_errors() {
        Assertions.assertFalse(response.getBody().isEmpty());
    }

    @Then("the response should contain a payment with the same id")
    public void the_response_should_contain_a_payment_with_the_same_id() throws Exception {
        System.out.println("Raw response: " + response.getBody());
        PaymentWithIdDTO payment = objectMapper.readValue(response.getBody(), PaymentWithIdDTO.class);
        System.out.println(savedPaymentId);
        System.out.println(payment.getId());
        Assertions.assertEquals(savedPaymentId, payment.getId());
    }

    @Then("the response should reflect the updated payment")
    public void the_response_should_reflect_the_updated_payment() throws Exception {
        PaymentWithIdDTO payment = objectMapper.readValue(response.getBody(), PaymentWithIdDTO.class);
        Assertions.assertEquals(200.0, payment.getPrice());
    }
}
