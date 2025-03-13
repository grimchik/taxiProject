package rideservice.kafkaservice.producer;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import rideservice.dto.CreatePaymentDTO;
import rideservice.exception.KafkaSendException;

import java.util.concurrent.TimeUnit;

@Service
public class CreatePaymentProducer {
    private final KafkaTemplate<String, CreatePaymentDTO> kafkaTemplate;

    @Value("${kafka.topic.createPaymentTopic}")
    private String createPaymentTopic;

    public CreatePaymentProducer(KafkaTemplate<String, CreatePaymentDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCreatePaymentRequest(@Valid CreatePaymentDTO request) {
        try {
            kafkaTemplate.send(createPaymentTopic, request).get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
