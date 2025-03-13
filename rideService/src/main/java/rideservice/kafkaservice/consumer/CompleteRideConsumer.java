package rideservice.kafkaservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rideservice.dto.CanceledRideByDriverDTO;
import rideservice.dto.CompleteRideDTO;
import rideservice.service.RideService;

@Service
public class CompleteRideConsumer {
    private final RideService rideService;

    @Autowired
    public  CompleteRideConsumer(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "${kafka.topic.completeRideTopic}", containerFactory = "kafkaCompleteRideListenerContainerFactory")
    public void listenCancelRide(CompleteRideDTO message) {
        rideService.completeRide(message);
    }
}
