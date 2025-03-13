package rideservice.kafkaservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rideservice.dto.CanceledRideByDriverDTO;
import rideservice.service.RideService;
@Service
public class CancelRideByDriverRequestConsumer
{
    private final RideService rideService;

    @Autowired
    public  CancelRideByDriverRequestConsumer(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "${kafka.topic.rideCancelByDriverTopic}", containerFactory = "driverCancelKafkaListenerContainerFactory")
    public void listenCancelRide(CanceledRideByDriverDTO message) {
        rideService.cancelRideByDriver(message);
    }

}
