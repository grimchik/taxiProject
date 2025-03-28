package paymentservice.integrationtest;

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
import paymentservice.dto.ConfirmedPaymentDTO;
import paymentservice.dto.PaymentDTO;
import paymentservice.dto.UpdatePaymentDTO;
import paymentservice.entity.Payment;
import paymentservice.repository.PaymentRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void setup() {
        paymentRepository.deleteAll();
    }

    @Test
    public void testUpdatePaymentWithPut() throws Exception {
        Payment payment = new Payment();
        payment.setPrice(150.0);
        payment.setPaymentType("CASH");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCardNumber(null);
        payment.setRideId(2L);
        payment.setUserId(20L);
        Payment saved = paymentRepository.save(payment);

        PaymentDTO updateDto = new PaymentDTO();
        updateDto.setPrice(175.0);
        updateDto.setPaymentType("CARD");
        updateDto.setCardNumber("5555-6666-7777-8888");

        mockMvc.perform(put("/api/v1/payments/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(175.0))
                .andExpect(jsonPath("$.paymentType").value("CARD"))
                .andExpect(jsonPath("$.cardNumber").value("5555-6666-7777-8888"));
    }

    @Test
    public void testChangePaymentWithPatch() throws Exception {
        Payment payment = new Payment();
        payment.setPrice(200.0);
        payment.setPaymentType("CASH");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCardNumber(null);
        payment.setRideId(3L);
        payment.setUserId(30L);
        Payment saved = paymentRepository.save(payment);

        UpdatePaymentDTO patchDto = new UpdatePaymentDTO();
        patchDto.setPrice(225.0);

        mockMvc.perform(patch("/api/v1/payments/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(225.0));
    }

    @Test
    public void testDeletePayment() throws Exception {
        Payment payment = new Payment();
        payment.setPrice(130.0);
        payment.setPaymentType("CASH");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCardNumber(null);
        payment.setRideId(4L);
        payment.setUserId(40L);
        Payment saved = paymentRepository.save(payment);

        mockMvc.perform(delete("/api/v1/payments/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/payments/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllPayments() throws Exception {
        Payment payment1 = new Payment();
        payment1.setPrice(80.0);
        payment1.setPaymentType("CASH");
        payment1.setPaymentDate(LocalDateTime.now());
        payment1.setCardNumber(null);
        payment1.setRideId(5L);
        payment1.setUserId(50L);
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setPrice(90.0);
        payment2.setPaymentType("CASH");
        payment2.setPaymentDate(LocalDateTime.now());
        payment2.setCardNumber(null);
        payment2.setRideId(6L);
        payment2.setUserId(60L);
        paymentRepository.save(payment2);

        mockMvc.perform(get("/api/v1/payments/")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(Matchers.greaterThanOrEqualTo(2))));
    }

    @Test
    public void testGetPaymentsByUser() throws Exception {
        Payment payment1 = new Payment();
        payment1.setPrice(110.0);
        payment1.setPaymentType("CASH");
        payment1.setPaymentDate(LocalDateTime.now());
        payment1.setCardNumber(null);
        payment1.setRideId(7L);
        payment1.setUserId(100L);
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setPrice(130.0);
        payment2.setPaymentType("CASH");
        payment2.setPaymentDate(LocalDateTime.now());
        payment2.setCardNumber(null);
        payment2.setRideId(8L);
        payment2.setUserId(100L);
        paymentRepository.save(payment2);

        mockMvc.perform(get("/api/v1/payments/user-payments/{userId}", 100)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void testGetPendingPaymentsByUser() throws Exception {
        Payment payment = new Payment();
        payment.setPrice(140.0);
        payment.setPaymentType("DEFAULT");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCardNumber(null);
        payment.setRideId(9L);
        payment.setUserId(111L);
        paymentRepository.save(payment);

        mockMvc.perform(get("/api/v1/payments/user-pending-payments/{userId}", 111))
                .andExpect(status().isOk());
    }
}