package promocodeservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import promocodeservice.dto.*;
import promocodeservice.entity.PromoCode;
import promocodeservice.kafkaservice.ApplyPromoCodeProducer;
import promocodeservice.mapper.PromoCodeMapper;
import promocodeservice.mapper.PromoCodeWithIdMapper;
import promocodeservice.repository.PromoCodeRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PromoCodeService {
    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper = PromoCodeMapper.INSTANCE;
    private final PromoCodeWithIdMapper promoCodeWithIdMapper = PromoCodeWithIdMapper.INSTANCE;

    private final ApplyPromoCodeProducer applyPromoCodeProducer;
    public PromoCodeService(PromoCodeRepository promoCodeRepository, ApplyPromoCodeProducer applyPromoCodeProducer)
    {
        this.promoCodeRepository=promoCodeRepository;
        this.applyPromoCodeProducer=applyPromoCodeProducer;
    }

    private void checkPromoCodeExist(PromoCode promoCode,String keyword)
    {
        if (promoCodeRepository.findByKeyword(keyword).isPresent() && !promoCode.getKeyword().equals(keyword))
        {
            throw new EntityExistsException("PromoCode with the same keyword already exist");
        }
    }

    @Transactional
    public PromoCodeWithIdDTO createPromoCode(PromoCodeDTO promoCodeDTO)
    {
        if (promoCodeRepository.findByKeyword(promoCodeDTO.getKeyword()).isPresent())
        {
            throw new EntityExistsException("PromoCode with the same keyword already exist");
        }
        PromoCode promoCode = promoCodeMapper.toEntity(promoCodeDTO);
        promoCodeRepository.save(promoCode);
        return promoCodeWithIdMapper.toDTO(promoCode);
    }

    public PromoCodeWithIdDTO getPromoCodeById (Long id)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        promoCodeOptional.orElseThrow(() -> new EntityNotFoundException("PromoCode not found"));
        return promoCodeWithIdMapper.toDTO(promoCodeOptional.get());
    }

    @Transactional
    public PromoCodeWithIdDTO updatePromoCode (Long id, PromoCodeDTO promoCodeDTO)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        promoCodeOptional.orElseThrow(() -> new EntityNotFoundException("PromoCode not found"));
        PromoCode promoCode = promoCodeOptional.get();
        checkPromoCodeExist(promoCode, promoCodeDTO.getKeyword());
        promoCode.setPercent(promoCodeDTO.getPercent());
        promoCode.setExpiryDate(promoCodeDTO.getExpiryDate());
        promoCode.setKeyword(promoCodeDTO.getKeyword());
        promoCode.setActivationDate(promoCodeDTO.getActivationDate());
        promoCodeRepository.save(promoCode);
        return promoCodeWithIdMapper.toDTO(promoCode);
    }
    @Transactional
    public PromoCodeWithIdDTO changePromoCode(Long id, UpdatePromoCodeDTO updatePromoCodeDTO) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PromoCode not found"));

        if (updatePromoCodeDTO.getKeyword() != null) {
            checkPromoCodeExist(promoCode, updatePromoCodeDTO.getKeyword());
            promoCode.setKeyword(updatePromoCodeDTO.getKeyword());
        }

        if (updatePromoCodeDTO.getActivationDate() != null) {
            promoCode.setActivationDate(updatePromoCodeDTO.getActivationDate());
        }

        if (updatePromoCodeDTO.getExpiryDate() != null) {
            promoCode.setExpiryDate(updatePromoCodeDTO.getExpiryDate());
        }

        if (promoCode.getActivationDate() != null && promoCode.getExpiryDate() != null &&
                promoCode.getExpiryDate().isBefore(promoCode.getActivationDate())) {
            throw new IllegalArgumentException("Expiry date must be after or equal to activation date");
        }

        if (updatePromoCodeDTO.getPercent() != null) {
            promoCode.setPercent(updatePromoCodeDTO.getPercent());
        }

        promoCodeRepository.save(promoCode);

        return promoCodeWithIdMapper.toDTO(promoCode);
    }



    @Transactional
    public void deletePromoCode (Long id)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        promoCodeOptional.orElseThrow(() -> new EntityNotFoundException("PromoCode not found"));
        promoCodeRepository.delete(promoCodeOptional.get());
    }

    public Page<PromoCodeWithIdDTO> getAllPromoCodes(Pageable pageable) {
        return promoCodeRepository.findAll(pageable).map(promoCodeWithIdMapper::toDTO);
    }

    public void checkPromoCode (CheckPromoCodeDTO checkPromoCodeDTO)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findByKeyword(checkPromoCodeDTO.getKeyword());
        PromoCode promoCode = promoCodeOptional.orElseThrow(() -> new EntityNotFoundException("PromoCode not found"));
        if (promoCode.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("PromoCode has expired");
        }

        ApplyPromocodeDTO applyPromocodeDTO = new ApplyPromocodeDTO (checkPromoCodeDTO.getUserId(),promoCode.getPercent());
        applyPromoCodeProducer.sendApplyPromoCodeRequest(applyPromocodeDTO);
    }
}
