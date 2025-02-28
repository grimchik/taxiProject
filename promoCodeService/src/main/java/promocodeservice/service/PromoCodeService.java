package promocodeservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import promocodeservice.dto.*;
import promocodeservice.entity.PromoCode;
import promocodeservice.mapper.PromoCodeMapper;
import promocodeservice.mapper.PromoCodeWithIdMapper;
import promocodeservice.repository.PromoCodeRepository;

import java.util.Optional;

@Service
public class PromoCodeService {
    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper = PromoCodeMapper.INSTANCE;
    private final PromoCodeWithIdMapper promoCodeWithIdMapper = PromoCodeWithIdMapper.INSTANCE;

    public PromoCodeService(PromoCodeRepository promoCodeRepository)
    {
        this.promoCodeRepository=promoCodeRepository;
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

    public PromoCodeWithIdDTO getPromoCodeByKeyword (KeywordDTO keywordDTO)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findByKeyword(keywordDTO.getKeyword());
        if (promoCodeOptional.isEmpty())
        {
            throw new EntityNotFoundException("PromoCode not found");
        }
        return promoCodeWithIdMapper.toDTO(promoCodeOptional.get());
    }

    public PromoCodeWithIdDTO getPromoCodeById (Long id)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        if (promoCodeOptional.isEmpty())
        {
            throw new EntityNotFoundException("PromoCode not found");
        }
        return promoCodeWithIdMapper.toDTO(promoCodeOptional.get());
    }

    @Transactional
    public String changeKeyWord (Long id, KeywordDTO keywordDTO)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        if (promoCodeOptional.isEmpty())
        {
            throw new EntityNotFoundException("PromoCode not found");
        }
        if (promoCodeRepository.findByKeyword(keywordDTO.getKeyword()).isPresent())
        {
            throw new EntityExistsException("PromoCode with the same keyword already exist");
        }
        PromoCode promoCode = promoCodeOptional.get();
        promoCode.setKeyword(keywordDTO.getKeyword());
        promoCodeRepository.save(promoCode);
        return "Keyword changed successfully";
    }

    @Transactional
    public String changeActivationDate (Long id, ActivationDateDTO activationDateDTO)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        if (promoCodeOptional.isEmpty())
        {
            throw new EntityNotFoundException("PromoCode not found");
        }
        PromoCode promoCode = promoCodeOptional.get();
        promoCode.setActivationDate(activationDateDTO.getActivationDate());
        promoCodeRepository.save(promoCode);
        return "Activation Date changed successfully";
    }

    @Transactional
    public String changeExpiryDate (Long id, ExpiryDateDTO expiryDateDTO)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        if (promoCodeOptional.isEmpty())
        {
            throw new EntityNotFoundException("PromoCode not found");
        }
        PromoCode promoCode = promoCodeOptional.get();
        promoCode.setExpiryDate(expiryDateDTO.getExpiryDate());
        promoCodeRepository.save(promoCode);
        return "Expiry Date changed successfully";
    }

    @Transactional
    public String changePercent (Long id, PercentDTO percentDTO) {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        if (promoCodeOptional.isEmpty()) {
            throw new EntityNotFoundException("PromoCode not found");
        }
        PromoCode promoCode = promoCodeOptional.get();
        promoCode.setPercent(percentDTO.getPercent());
        promoCodeRepository.save(promoCode);
        return "Percent changed successfully";
    }

    @Transactional
    public PromoCodeWithIdDTO updatePromoCode (Long id, PromoCodeDTO promoCodeDTO)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        if (promoCodeOptional.isEmpty())
        {
            throw new EntityNotFoundException("PromoCode not found");
        }
        PromoCode promoCode = promoCodeOptional.get();
        if (promoCodeRepository.findByKeyword(promoCodeDTO.getKeyword()).isPresent() && !promoCode.getKeyword().equals(promoCodeDTO.getKeyword()))
        {
            throw new EntityExistsException("PromoCode with the same keyword already exist");
        }
        promoCode.setPercent(promoCodeDTO.getPercent());
        promoCode.setExpiryDate(promoCodeDTO.getExpiryDate());
        promoCode.setKeyword(promoCodeDTO.getKeyword());
        promoCode.setActivationDate(promoCodeDTO.getActivationDate());
        promoCodeRepository.save(promoCode);
        return promoCodeWithIdMapper.toDTO(promoCode);
    }

    @Transactional
    public void deletePromoCode (Long id)
    {
        Optional<PromoCode> promoCodeOptional = promoCodeRepository.findById(id);
        if (promoCodeOptional.isEmpty())
        {
            throw new EntityNotFoundException("PromoCode not found");
        }
        promoCodeRepository.delete(promoCodeOptional.get());
    }
}
