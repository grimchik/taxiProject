package driverservice.kafkaservice;

import driverservice.dto.CanceledRideByDriverDTO;
import driverservice.dto.FinishRideDTO;
import driverservice.exception.KafkaSendException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class FinishRideProducer {
    private final KafkaTemplate<String, FinishRideDTO> kafkaTemplate;

    @Value("${kafka.topic.finishRideTopic}")
    private String finishRideTopic;

    public FinishRideProducer(KafkaTemplate<String, FinishRideDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendFinishRequest(@Valid FinishRideDTO request) {
        try {
            kafkaTemplate.send(finishRideTopic, request).get(10, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
