package paymentservice.kafkaservice;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import paymentservice.dto.CompleteRideDTO;
import paymentservice.exception.KafkaSendException;

import java.util.concurrent.TimeUnit;

@Service
public class CompleteRideProducer {
    private final KafkaTemplate<String, CompleteRideDTO> kafkaTemplate;

    @Value("${kafka.topic.completeRideTopic}")
    private String completeRideTopic;

    public CompleteRideProducer(KafkaTemplate<String, CompleteRideDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCancelRequest(@Valid CompleteRideDTO request) {
        try {
            kafkaTemplate.send(completeRideTopic, request).get(10, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
