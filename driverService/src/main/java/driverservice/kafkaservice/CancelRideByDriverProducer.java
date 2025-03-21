package driverservice.kafkaservice;

import driverservice.dto.CanceledRideByDriverDTO;
import driverservice.exception.KafkaSendException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class CancelRideByDriverProducer {

    private final KafkaTemplate<String, CanceledRideByDriverDTO> kafkaTemplate;

    @Value("${kafka.topic.rideCancelByDriverTopic}")
    private String rideCancelByDriverTopic;

    public CancelRideByDriverProducer(KafkaTemplate<String, CanceledRideByDriverDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCancelRequest(@Valid CanceledRideByDriverDTO request) {
        try {
            kafkaTemplate.send(rideCancelByDriverTopic, request).get(10, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}

