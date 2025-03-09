package clientservice.kafkaservice;

import clientservice.dto.GetRidesRequestDTO;
import clientservice.dto.RidePageResponse;
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
public class GetRideProducer {
    private final ReplyingKafkaTemplate<String, GetRidesRequestDTO, RidePageResponse> replyingKafkaTemplate;

    @Value("${kafka.topic.rideGetTopic}")
    private String rideGetTopic;

    @Value("${kafka.topic.getRideReplyTopic}")
    private String getRideReplyTopic;

    public GetRideProducer(ReplyingKafkaTemplate<String, GetRidesRequestDTO, RidePageResponse> replyingKafkaTemplate) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    public RidePageResponse sendRideRequest(GetRidesRequestDTO request) throws Exception {
        ProducerRecord<String, GetRidesRequestDTO> record = new ProducerRecord<>(rideGetTopic, request);
        record.headers().add(KafkaHeaders.REPLY_TOPIC, getRideReplyTopic.getBytes());
        RequestReplyFuture<String, GetRidesRequestDTO, RidePageResponse> future = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, RidePageResponse> response = future.get(10, TimeUnit.SECONDS);
        return response.value();
    }
}


