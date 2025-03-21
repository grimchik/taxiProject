package rideservice.kafkaservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rideservice.dto.RideInProgressDTO;
import rideservice.service.RideService;

@Service
public class RideInProgressConsumer {
    private final RideService rideService;

    @Autowired
    public  RideInProgressConsumer(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "${kafka.topic.rideInProgressTopic}", containerFactory = "rideInProgressKafkaListenerContainerFactory")
    public void listenCancelRide(RideInProgressDTO message) {
        rideService.rideInProgress(message);
    }
}
