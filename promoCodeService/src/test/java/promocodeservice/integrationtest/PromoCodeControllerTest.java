package promocodeservice.integrationtest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import promocodeservice.dto.PromoCodeDTO;
import promocodeservice.dto.UpdatePromoCodeDTO;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PromoCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAndGetPromoCode() throws Exception {
        PromoCodeDTO promoCodeDTO = new PromoCodeDTO();
        promoCodeDTO.setKeyword("PROMO100");
        promoCodeDTO.setPercent(10L);
        promoCodeDTO.setActivationDate(LocalDate.now());
        promoCodeDTO.setExpiryDate(LocalDate.now().plusMonths(2));

        String postResponse = mockMvc.perform(post("/api/v1/promocodes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promoCodeDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.keyword").value("PROMO100"))
                .andReturn().getResponse().getContentAsString();

        JsonNode postJson = objectMapper.readTree(postResponse);
        Long id = postJson.get("id").asLong();

        mockMvc.perform(get("/api/v1/promocodes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keyword").value("PROMO100"))
                .andExpect(jsonPath("$.percent").value(10));
    }

    @Test
    public void testUpdatePromoCodeWithPut() throws Exception {
        PromoCodeDTO promoCodeDTO = new PromoCodeDTO();
        promoCodeDTO.setKeyword("PUTPROMO");
        promoCodeDTO.setPercent(20L);
        promoCodeDTO.setActivationDate(LocalDate.now());
        promoCodeDTO.setExpiryDate(LocalDate.now().plusMonths(2));

        String createResponse = mockMvc.perform(post("/api/v1/promocodes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promoCodeDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        PromoCodeDTO updatedDto = new PromoCodeDTO();
        updatedDto.setKeyword("PUTPROMO_UPDATED");
        updatedDto.setPercent(25L);
        updatedDto.setActivationDate(LocalDate.now());
        updatedDto.setExpiryDate(LocalDate.now().plusMonths(2));

        mockMvc.perform(put("/api/v1/promocodes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keyword").value("PUTPROMO_UPDATED"))
                .andExpect(jsonPath("$.percent").value(25));
    }

    @Test
    public void testUpdatePromoCodeWithPatch() throws Exception {
        PromoCodeDTO promoCodeDTO = new PromoCodeDTO();
        promoCodeDTO.setKeyword("PATCHPROMO");
        promoCodeDTO.setPercent(30L);
        promoCodeDTO.setActivationDate(LocalDate.now());
        promoCodeDTO.setExpiryDate(LocalDate.now().plusMonths(2));

        String createResponse = mockMvc.perform(post("/api/v1/promocodes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promoCodeDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        UpdatePromoCodeDTO updateDto = new UpdatePromoCodeDTO();
        updateDto.setPercent(35L);
        updateDto.setKeyword("PATCHPROMO_UPDATED");
        updateDto.setActivationDate(LocalDate.now());
        updateDto.setExpiryDate(LocalDate.now().plusMonths(2));

        mockMvc.perform(patch("/api/v1/promocodes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keyword").value("PATCHPROMO_UPDATED"))
                .andExpect(jsonPath("$.percent").value(35));
    }

    @Test
    public void testDeletePromoCode() throws Exception {
        PromoCodeDTO promoCodeDTO = new PromoCodeDTO();
        promoCodeDTO.setKeyword("DELETEPROMO");
        promoCodeDTO.setPercent(40L);
        promoCodeDTO.setActivationDate(LocalDate.of(2023, 8, 1));
        promoCodeDTO.setExpiryDate(LocalDate.of(2023, 12, 31));

        String createResponse = mockMvc.perform(post("/api/v1/promocodes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promoCodeDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/v1/promocodes/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/promocodes/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllPromoCodes() throws Exception {
        PromoCodeDTO promo1 = new PromoCodeDTO();
        promo1.setKeyword("ALLPROMO1");
        promo1.setPercent(15L);
        promo1.setActivationDate(LocalDate.of(2023, 10, 1));
        promo1.setExpiryDate(LocalDate.of(2023, 11, 1));

        PromoCodeDTO promo2 = new PromoCodeDTO();
        promo2.setKeyword("ALLPROMO2");
        promo2.setPercent(20L);
        promo2.setActivationDate(LocalDate.of(2023, 10, 5));
        promo2.setExpiryDate(LocalDate.of(2023, 11, 5));

        mockMvc.perform(post("/api/v1/promocodes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promo1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/promocodes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(promo2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/promocodes/")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()", Matchers.greaterThanOrEqualTo(2)));
    }
}
