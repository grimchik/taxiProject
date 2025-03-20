package promocodeservice.unittest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import promocodeservice.dto.*;
import promocodeservice.entity.PromoCode;
import promocodeservice.kafkaservice.ApplyPromoCodeProducer;
import promocodeservice.mapper.PromoCodeMapper;
import promocodeservice.mapper.PromoCodeWithIdMapper;
import promocodeservice.repository.PromoCodeRepository;
import promocodeservice.service.PromoCodeService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromoCodeServiceTest {

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private ApplyPromoCodeProducer applyPromoCodeProducer;

    @Mock
    private PromoCodeMapper promoCodeMapper;

    @Mock
    private PromoCodeWithIdMapper promoCodeWithIdMapper;

    @InjectMocks
    private PromoCodeService promoCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePromoCode_AlreadyExists() {
        PromoCodeDTO promoCodeDTO = new PromoCodeDTO();
        promoCodeDTO.setKeyword("DISCOUNT10");

        when(promoCodeRepository.findByKeyword(promoCodeDTO.getKeyword())).thenReturn(Optional.of(new PromoCode()));

        assertThrows(EntityExistsException.class, () -> promoCodeService.createPromoCode(promoCodeDTO));
    }

    @Test
    void testGetPromoCodeById_Success() {
        Long promoCodeId = 1L;
        PromoCode promoCode = new PromoCode();
        PromoCodeWithIdDTO promoCodeWithIdDTO = new PromoCodeWithIdDTO();

        when(promoCodeRepository.findById(promoCodeId)).thenReturn(Optional.of(promoCode));
        when(promoCodeWithIdMapper.toDTO(promoCode)).thenReturn(promoCodeWithIdDTO);

        PromoCodeWithIdDTO result = promoCodeService.getPromoCodeById(promoCodeId);

        assertNotNull(result);
        verify(promoCodeRepository, times(1)).findById(promoCodeId);
    }

    @Test
    void testGetPromoCodeById_NotFound() {
        Long promoCodeId = 1L;

        when(promoCodeRepository.findById(promoCodeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promoCodeService.getPromoCodeById(promoCodeId));
    }

    @Test
    void testDeletePromoCode_Success() {
        Long promoCodeId = 1L;
        PromoCode promoCode = new PromoCode();

        when(promoCodeRepository.findById(promoCodeId)).thenReturn(Optional.of(promoCode));

        promoCodeService.deletePromoCode(promoCodeId);

        verify(promoCodeRepository, times(1)).delete(promoCode);
    }

    @Test
    void testDeletePromoCode_NotFound() {
        Long promoCodeId = 1L;

        when(promoCodeRepository.findById(promoCodeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promoCodeService.deletePromoCode(promoCodeId));
    }

    @Test
    void testUpdatePromoCode_Success() {
        Long promoCodeId = 1L;
        PromoCodeDTO promoCodeDTO = new PromoCodeDTO();
        promoCodeDTO.setKeyword("DISCOUNT20");
        promoCodeDTO.setPercent(20L);
        promoCodeDTO.setActivationDate(LocalDate.now());
        promoCodeDTO.setExpiryDate(LocalDate.now().plusDays(10));

        PromoCode promoCode = new PromoCode();
        PromoCodeWithIdDTO promoCodeWithIdDTO = new PromoCodeWithIdDTO();

        when(promoCodeRepository.findById(promoCodeId)).thenReturn(Optional.of(promoCode));
        when(promoCodeRepository.findByKeyword(promoCodeDTO.getKeyword())).thenReturn(Optional.empty());
        when(promoCodeRepository.save(promoCode)).thenReturn(promoCode);
        when(promoCodeWithIdMapper.toDTO(promoCode)).thenReturn(promoCodeWithIdDTO);

        PromoCodeWithIdDTO result = promoCodeService.updatePromoCode(promoCodeId, promoCodeDTO);

        assertNotNull(result);
        verify(promoCodeRepository, times(1)).save(promoCode);
    }

    @Test
    void testCheckPromoCode_Success() {
        CheckPromoCodeDTO checkPromoCodeDTO = new CheckPromoCodeDTO();
        checkPromoCodeDTO.setKeyword("DISCOUNT10");
        checkPromoCodeDTO.setUserId(1L);

        PromoCode promoCode = new PromoCode();
        promoCode.setPercent(10L);
        promoCode.setExpiryDate(LocalDate.now().plusDays(5));

        when(promoCodeRepository.findByKeyword(checkPromoCodeDTO.getKeyword())).thenReturn(Optional.of(promoCode));

        promoCodeService.checkPromoCode(checkPromoCodeDTO);

        verify(applyPromoCodeProducer, times(1)).sendApplyPromoCodeRequest(any(ApplyPromocodeDTO.class));
    }

    @Test
    void testCheckPromoCode_Expired() {
        CheckPromoCodeDTO checkPromoCodeDTO = new CheckPromoCodeDTO();
        checkPromoCodeDTO.setKeyword("DISCOUNT10");

        PromoCode promoCode = new PromoCode();
        promoCode.setExpiryDate(LocalDate.now().minusDays(1));

        when(promoCodeRepository.findByKeyword(checkPromoCodeDTO.getKeyword())).thenReturn(Optional.of(promoCode));

        assertThrows(IllegalStateException.class, () -> promoCodeService.checkPromoCode(checkPromoCodeDTO));
    }
}
