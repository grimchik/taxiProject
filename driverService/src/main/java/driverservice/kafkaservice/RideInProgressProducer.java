package driverservice.kafkaservice;

import driverservice.dto.RideInProgressDTO;
import driverservice.exception.KafkaSendException;
import io.micrometer.tracing.Tracer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RideInProgressProducer {

    private static final Logger log = LoggerFactory.getLogger(RideInProgressProducer.class);
    private final KafkaTemplate<String, RideInProgressDTO> kafkaTemplate;
    private final Tracer tracer;

    @Value("${kafka.topic.rideInProgressTopic}")
    private String rideInProgressTopic;

    public RideInProgressProducer(KafkaTemplate<String, RideInProgressDTO> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendRideInProgressRequest(@Valid RideInProgressDTO request) {
        try {
            String traceId = tracer.currentTraceContext().context().traceId();

            Message<RideInProgressDTO> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.TOPIC, rideInProgressTopic)
                    .setHeader("traceId", traceId)
                    .build();

            log.info("Sending RideInProgress message to Kafka with data={} and traceId={}", request, traceId);

            kafkaTemplate.send(message).get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("Failed to send ride in progress message to Kafka", ex);
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}