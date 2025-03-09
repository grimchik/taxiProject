package rideservice.kafkaservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import rideservice.dto.*;
import rideservice.service.RideService;

@Service
public class CancelRideRequestConsumer {

    private final RideService rideService;
    private final KafkaTemplate<String, RideWithIdDTO> kafkaTemplate;

    @Autowired
    public  CancelRideRequestConsumer(RideService rideService,@Qualifier("cancelRideKafkaTemplate") KafkaTemplate<String, RideWithIdDTO> kafkaTemplate) {
        this.rideService = rideService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "rideCancelTopic", groupId = "cancel-ride-group",containerFactory = "cancelKafkaListenerContainerFactory")
    @SendTo("cancelRideReplyTopic")
    public RideWithIdDTO consumeMessage(CanceledRideDTO canceledRideDTO, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        return rideService.cancelRide(canceledRideDTO);
    }
}
