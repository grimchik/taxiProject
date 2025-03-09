package rideservice.kafkaservice;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.SendTo;
import rideservice.dto.GetRidesRequestDTO;
import rideservice.dto.RideDTO;
import rideservice.dto.RideWithIdDTO;
import rideservice.service.RideService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class RideRequestConsumer {

    private final RideService rideService;
    private final KafkaTemplate<String, RideWithIdDTO> kafkaTemplate;

    @Autowired
    public RideRequestConsumer(RideService rideService, @Qualifier("rideKafkaTemplate") KafkaTemplate<String, RideWithIdDTO> kafkaTemplate) {
        this.rideService = rideService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "rideCreateTopic", groupId = "ride-group")
    @SendTo("rideReplyTopic")
    public RideWithIdDTO consumeMessage(RideDTO rideDTO, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        return rideService.createRide(rideDTO);
    }
}