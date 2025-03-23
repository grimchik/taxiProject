package promocodeservice.integrationtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import promocodeservice.entity.PromoCode;
import promocodeservice.repository.PromoCodeRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PromoCodeRepositoryTest {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Test
    public void testSavePromoCode() {
        PromoCode promoCode = new PromoCode();
        promoCode.setKeyword("TESTSAVE");
        promoCode.setPercent(15L);
        promoCode.setActivationDate(LocalDate.now());
        promoCode.setExpiryDate(LocalDate.now().plusMonths(1));
        promoCodeRepository.save(promoCode);
        assertThat(promoCode.getId()).isNotNull();
    }

    @Test
    public void testFindByKeyword() {
        PromoCode promoCode = new PromoCode();
        promoCode.setKeyword("FINDTEST");
        promoCode.setPercent(20L);
        promoCode.setActivationDate(LocalDate.now());
        promoCode.setExpiryDate(LocalDate.now().plusMonths(2));
        promoCodeRepository.save(promoCode);

        PromoCode foundPromoCode = promoCodeRepository.findByKeyword("FINDTEST").orElse(null);
        assertThat(foundPromoCode).isNotNull();
        assertThat(foundPromoCode.getPercent()).isEqualTo(20L);
    }

    @Test
    public void testUpdatePromoCode() {
        PromoCode promoCode = new PromoCode();
        promoCode.setKeyword("UPDATETEST");
        promoCode.setPercent(10L);
        promoCode.setActivationDate(LocalDate.now());
        promoCode.setExpiryDate(LocalDate.now().plusMonths(1));
        promoCode = promoCodeRepository.save(promoCode);

        promoCode.setPercent(12L);
        promoCode.setExpiryDate(LocalDate.now().plusMonths(2));
        promoCodeRepository.save(promoCode);

        PromoCode updatedPromoCode = promoCodeRepository.findById(promoCode.getId()).orElse(null);
        assertThat(updatedPromoCode).isNotNull();
        assertThat(updatedPromoCode.getPercent()).isEqualTo(12L);
        assertThat(updatedPromoCode.getExpiryDate()).isEqualTo(LocalDate.now().plusMonths(2));
    }

    @Test
    public void testDeletePromoCode() {
        PromoCode promoCode = new PromoCode();
        promoCode.setKeyword("DELETETEST");
        promoCode.setPercent(5L);
        promoCode.setActivationDate(LocalDate.now());
        promoCode.setExpiryDate(LocalDate.now().plusWeeks(1));
        promoCode = promoCodeRepository.save(promoCode);
        Long id = promoCode.getId();
        promoCodeRepository.delete(promoCode);
        PromoCode deletedPromoCode = promoCodeRepository.findById(id).orElse(null);
        assertThat(deletedPromoCode).isNull();
    }
}