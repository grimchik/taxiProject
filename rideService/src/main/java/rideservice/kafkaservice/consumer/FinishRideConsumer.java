package rideservice.kafkaservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rideservice.dto.FinishRideDTO;
import rideservice.service.RideService;

@Service
public class FinishRideConsumer {
    private final RideService rideService;

    @Autowired
    public  FinishRideConsumer(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "${kafka.topic.finishRideTopic}", containerFactory = "kafkaFinishRideListenerContainerFactory")
    public void listenCancelRide(FinishRideDTO message) {
        rideService.finishRide(message);
    }
}
