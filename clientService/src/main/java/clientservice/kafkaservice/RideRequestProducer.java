package clientservice.kafkaservice;

import clientservice.dto.CreateRideRequestDTO;
import clientservice.dto.RideWithIdDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RideRequestProducer {
    private final ReplyingKafkaTemplate<String, CreateRideRequestDTO, RideWithIdDTO> replyingKafkaTemplate;

    @Value("${kafka.topic.rideReplyTopic}")
    private String rideReplyTopic;

    @Value("${kafka.topic.rideCreateTopic}")
    private String rideCreateTopic;

    public RideRequestProducer(ReplyingKafkaTemplate<String, CreateRideRequestDTO, RideWithIdDTO> replyingKafkaTemplate) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    public RideWithIdDTO sendRideRequest(CreateRideRequestDTO request) throws Exception {
        ProducerRecord<String, CreateRideRequestDTO> record = new ProducerRecord<>(rideCreateTopic, request);
        record.headers().add(KafkaHeaders.REPLY_TOPIC, rideReplyTopic.getBytes());
        RequestReplyFuture<String, CreateRideRequestDTO, RideWithIdDTO> future = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, RideWithIdDTO> response = future.get(10, TimeUnit.SECONDS);
        return response.value();
    }
}


