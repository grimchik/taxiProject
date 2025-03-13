package rideservice.kafkaservice.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rideservice.dto.ApplyPromocodeDTO;
import rideservice.service.RideService;

@Service
public class ApplyPromoCodeConsumer {
    private final RideService rideService;

    @Autowired
    public  ApplyPromoCodeConsumer(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "${kafka.topic.applyPromoCodeTopic}", containerFactory = "kafkaApplyPromoCodeListenerContainerFactory")
    public void listenCancelRide(ApplyPromocodeDTO message) {
        rideService.applyPromoCode(message);
    }
}