package driverservice.kafkaservice;

import driverservice.dto.CanceledRideByDriverDTO;
import driverservice.dto.RideInProgressDTO;
import driverservice.exception.KafkaSendException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RideInProgressProducer {
    private final KafkaTemplate<String, RideInProgressDTO> kafkaTemplate;

    @Value("${kafka.topic.rideInProgressTopic}")
    private String rideInProgressTopic;

    public RideInProgressProducer(KafkaTemplate<String, RideInProgressDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRideInProgressRequest(@Valid RideInProgressDTO request) {
        try {
            kafkaTemplate.send(rideInProgressTopic, request).get(10, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
