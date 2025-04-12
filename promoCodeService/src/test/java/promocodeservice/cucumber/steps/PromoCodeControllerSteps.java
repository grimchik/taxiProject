package promocodeservice.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import promocodeservice.dto.PromoCodeDTO;
import promocodeservice.dto.PromoCodeWithIdDTO;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("test")
public class PromoCodeControllerSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;
    private String payload;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private Long savedPromoCodeId;

    @Given("a valid promo code payload:")
    public void a_valid_promo_code_payload(String payload) {
        this.payload = payload;
    }

    @Given("a promo code payload with invalid percent:")
    public void a_promo_code_payload_with_invalid_percent(String payload) {
        this.payload = payload;
    }

    @Given("an existing promo code exists")
    public void an_existing_promo_code_exists() throws Exception {
        int randomNum = ThreadLocalRandom.current().nextInt(100, 1000);
        String uniqueKeyword = "PROMO" + randomNum;

        PromoCodeDTO promoCodeDTO = new PromoCodeDTO(
                20L,
                LocalDate.of(2024, 10, 1),
                uniqueKeyword,
                LocalDate.of(2025, 12, 31)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PromoCodeDTO> request = new HttpEntity<>(promoCodeDTO, headers);
        ResponseEntity<PromoCodeWithIdDTO> postResponse = restTemplate.postForEntity("/api/v1/promocodes/", request, PromoCodeWithIdDTO.class);
        Assertions.assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        savedPromoCodeId = postResponse.getBody().getId();
        Assertions.assertNotNull(savedPromoCodeId);
    }

    private String resolveUrl(String url) {
        return url.contains("{id}") ? url.replace("{id}", savedPromoCodeId.toString()) : url;
    }

    @Given("a payload to update the promo code:")
    public void a_payload_to_update_the_promo_code(String payload) {
        this.payload = payload;
    }

    @Given("a payload to partially update the promo code:")
    public void a_payload_to_partially_update_the_promo_code(String payload) {
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

    @Then("the response should contain the created promo code with an id")
    public void the_response_should_contain_the_created_promo_code_with_an_id() throws Exception {
        PromoCodeWithIdDTO promoCode = objectMapper.readValue(response.getBody(), PromoCodeWithIdDTO.class);
        Assertions.assertNotNull(promoCode.getId());
        Assertions.assertTrue(promoCode.getKeyword().startsWith("PROMO"));
    }

    @Then("the response should contain validation errors")
    public void the_response_should_contain_validation_errors() {
        Assertions.assertFalse(response.getBody().contains("must"));
    }

    @Then("the response should contain a promo code with the same id")
    public void the_response_should_contain_a_promo_code_with_the_same_id() throws Exception {
        PromoCodeWithIdDTO promoCode = objectMapper.readValue(response.getBody(), PromoCodeWithIdDTO.class);
        Assertions.assertEquals(savedPromoCodeId, promoCode.getId());
    }

    @Then("the response should reflect the updated promo code")
    public void the_response_should_reflect_the_updated_promo_code() throws Exception {
        PromoCodeWithIdDTO promoCode = objectMapper.readValue(response.getBody(), PromoCodeWithIdDTO.class);
        Assertions.assertEquals(30L, promoCode.getPercent());
    }

    @Then("the response should reflect the updated promo code with new keyword")
    public void the_response_should_reflect_the_updated_promo_code_with_new_keyword() throws Exception {
        PromoCodeWithIdDTO promoCode = objectMapper.readValue(response.getBody(), PromoCodeWithIdDTO.class);
        Assertions.assertTrue(promoCode.getKeyword().contains("NEW"));
    }
}
