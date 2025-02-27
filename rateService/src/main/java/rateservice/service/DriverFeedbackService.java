package rateservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rateservice.dto.DriverFeedbackDTO;
import rateservice.dto.DriverFeedbackWithIdDTO;
import rateservice.dto.RateDTO;
import rateservice.entity.DriverFeedback;
import rateservice.mapper.DriverFeedbackMapper;
import rateservice.mapper.DriverFeedbackWithIdMapper;
import rateservice.repository.DriverFeedbackRepository;

import java.util.Optional;

@Service
public class DriverFeedbackService {
    private final DriverFeedbackRepository driverFeedbackRepository;
    private final DriverFeedbackMapper driverFeedbackMapper= DriverFeedbackMapper.INSTANCE;
    private final DriverFeedbackWithIdMapper driverFeedbackWithIdMapper = DriverFeedbackWithIdMapper.INSTANCE;

    public DriverFeedbackService(DriverFeedbackRepository driverFeedbackRepository)
    {
        this.driverFeedbackRepository=driverFeedbackRepository;
    }

    @Transactional
    public DriverFeedbackWithIdDTO createFeedback(DriverFeedbackDTO driverFeedbackDTO)
    {
        DriverFeedback driverFeedback=new DriverFeedback();
        driverFeedback.setRate(driverFeedbackDTO.getRate());
        driverFeedback.setComment(driverFeedbackDTO.getComment());
        driverFeedback.setPunctuality(driverFeedbackDTO.getPunctuality());
        driverFeedback.setCleanPassenger(driverFeedbackDTO.getCleanPassenger());
        driverFeedback.setPolitePassenger(driverFeedbackDTO.getPolitePassenger());
        driverFeedbackRepository.save(driverFeedback);
        return driverFeedbackWithIdMapper.toDTO(driverFeedback);
    }

    public DriverFeedbackWithIdDTO getFeedback(Long id)
    {
        Optional<DriverFeedback> driverFeedbackOptional = driverFeedbackRepository.findById(id);
        if (driverFeedbackOptional.isEmpty())
        {
            throw new EntityNotFoundException("Feedback from driver not found");
        }
        return driverFeedbackWithIdMapper.toDTO(driverFeedbackOptional.get());
    }

    @Transactional
    public String changeRate(Long id, RateDTO rateDTO)
    {
        Optional<DriverFeedback> driverFeedbackOptional = driverFeedbackRepository.findById(id);
        if (driverFeedbackOptional.isEmpty())
        {
            throw new EntityNotFoundException("Feedback from driver not found");
        }
        DriverFeedback driverFeedback=driverFeedbackOptional.get();
        driverFeedback.setRate(rateDTO.getRate());
        return "Rate changed successfully";
    }

    @Transactional
    public void deleteFeedback(Long id)
    {
        Optional<DriverFeedback> driverFeedbackOptional = driverFeedbackRepository.findById(id);
        if (driverFeedbackOptional.isEmpty())
        {
            throw new EntityNotFoundException("Feedback from driver not found");
        }
        driverFeedbackRepository.delete(driverFeedbackOptional.get());
    }

    @Transactional
    public DriverFeedbackWithIdDTO updateFeedback(Long id, DriverFeedbackDTO driverFeedbackDTO)
    {
        Optional<DriverFeedback> driverFeedbackOptional = driverFeedbackRepository.findById(id);
        if (driverFeedbackOptional.isEmpty())
        {
            throw new EntityNotFoundException("Feedback from driver not found");
        }
        DriverFeedback driverFeedback = driverFeedbackOptional.get();
        driverFeedback.setRate(driverFeedbackDTO.getRate());
        driverFeedback.setComment(driverFeedbackDTO.getComment());
        driverFeedback.setPunctuality(driverFeedbackDTO.getPunctuality());
        driverFeedback.setCleanPassenger(driverFeedbackDTO.getCleanPassenger());
        driverFeedback.setPolitePassenger(driverFeedbackDTO.getPolitePassenger());
        driverFeedbackRepository.save(driverFeedback);
        return driverFeedbackWithIdMapper.toDTO(driverFeedback);
    }
}
