package clientservice.kafkaservice;

import clientservice.dto.CanceledRideDTO;
import clientservice.dto.RideWithIdDTO;
import clientservice.exception.KafkaSendException;
import jakarta.validation.Valid;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CancelRideProducer {
    private final KafkaTemplate<String, CanceledRideDTO> kafkaTemplate;

    @Value("${kafka.topic.rideCancelTopic}")
    private String rideCancelTopic;

    public CancelRideProducer(KafkaTemplate<String, CanceledRideDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCancelRequest(@Valid CanceledRideDTO request) {
        try {
            kafkaTemplate.send(rideCancelTopic, request).get(10, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
