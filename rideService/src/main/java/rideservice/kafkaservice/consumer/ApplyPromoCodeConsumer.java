package rideservice.kafkaservice.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import rideservice.dto.ApplyPromocodeDTO;
import rideservice.service.RideService;

@Service
public class ApplyPromoCodeConsumer {

    private static final Logger log = LoggerFactory.getLogger(ApplyPromoCodeConsumer.class);
    private final RideService rideService;

    @Autowired
    public ApplyPromoCodeConsumer(RideService rideService) {
        this.rideService = rideService;
    }

    @KafkaListener(topics = "${kafka.topic.applyPromoCodeTopic}", containerFactory = "kafkaApplyPromoCodeListenerContainerFactory")
    public void listenCancelRide(ConsumerRecord<String, ApplyPromocodeDTO> record) {
        log.info("Received ApplyPromoCode message with payload={}", record.value());

        String traceId = record.headers().lastHeader("traceId") != null ? new String(record.headers().lastHeader("traceId").value()) : "N/A";
        String spanId = record.headers().lastHeader("spanId") != null ? new String(record.headers().lastHeader("spanId").value()) : "N/A";


        log.info("Received message with traceId={} and spanId={}", traceId, spanId);
        rideService.applyPromoCode(record.value());
    }
}
