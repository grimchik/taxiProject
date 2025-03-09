package rideservice.kafkaservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.SendTo;
import rideservice.dto.GetRidesRequestDTO;
import rideservice.dto.RidePageResponse;
import rideservice.dto.RideWithIdDTO;
import rideservice.service.RideService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class GetRideRequestConsumer {

    private final RideService rideService;
    private final KafkaTemplate<String, RidePageResponse> kafkaTemplate;

    @Autowired
    public GetRideRequestConsumer(RideService rideService, KafkaTemplate<String, RidePageResponse> kafkaTemplate) {
        this.rideService = rideService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "rideGetTopic", groupId = "get-ride-group",containerFactory = "getKafkaListenerContainerFactory")
    @SendTo("getRideReplyTopic")
    public RidePageResponse consumeMessage(GetRidesRequestDTO getRidesRequestDTO, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        Pageable pageable = PageRequest.of(getRidesRequestDTO.getPage(), getRidesRequestDTO.getSize());
        Page<RideWithIdDTO> page = rideService.getAllRides(getRidesRequestDTO.getUserId(), pageable);
        return new RidePageResponse(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}