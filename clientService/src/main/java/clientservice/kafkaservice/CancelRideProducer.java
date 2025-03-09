package clientservice.kafkaservice;

import clientservice.dto.CanceledRideDTO;
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
public class CancelRideProducer {
    private final ReplyingKafkaTemplate<String, CanceledRideDTO, RideWithIdDTO> replyingKafkaTemplate;

    @Value("${kafka.topic.rideCancelTopic}")
    private String rideCancelTopic;

    @Value("${kafka.topic.cancelRideReplyTopic}")
    private String cancelRideReplyTopic;

    public CancelRideProducer(ReplyingKafkaTemplate<String, CanceledRideDTO, RideWithIdDTO> replyingKafkaTemplate) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    public RideWithIdDTO sendCancelRequest(CanceledRideDTO request) throws Exception {
        ProducerRecord<String, CanceledRideDTO> record = new ProducerRecord<>(rideCancelTopic, request);
        record.headers().add(KafkaHeaders.REPLY_TOPIC, cancelRideReplyTopic.getBytes());
        RequestReplyFuture<String, CanceledRideDTO, RideWithIdDTO> future = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, RideWithIdDTO> response = future.get(10, TimeUnit.SECONDS);
        return response.value();
    }
}
