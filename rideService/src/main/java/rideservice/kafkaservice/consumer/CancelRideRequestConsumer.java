package rideservice.kafkaservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rideservice.dto.*;
import rideservice.service.RideService;

@Service
public class CancelRideRequestConsumer {

    private final RideService rideService;

    @Autowired
    public  CancelRideRequestConsumer(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "${kafka.topic.rideCancelTopic}", containerFactory = "kafkaListenerContainerFactory")
    public void listenCancelRide(CanceledRideDTO message) {
        rideService.cancelRide(message);
    }
}
