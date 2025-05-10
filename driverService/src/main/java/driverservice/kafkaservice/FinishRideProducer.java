package driverservice.kafkaservice;

import driverservice.dto.FinishRideDTO;
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
public class FinishRideProducer {

    private static final Logger log = LoggerFactory.getLogger(FinishRideProducer.class);
    private final KafkaTemplate<String, FinishRideDTO> kafkaTemplate;
    private final Tracer tracer;

    @Value("${kafka.topic.finishRideTopic}")
    private String finishRideTopic;

    public FinishRideProducer(KafkaTemplate<String, FinishRideDTO> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendFinishRequest(@Valid FinishRideDTO request) {
        try {
            String traceId = tracer.currentTraceContext().context().traceId();

            Message<FinishRideDTO> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.TOPIC, finishRideTopic)
                    .setHeader("traceId", traceId)
                    .build();

            log.info("Sending FinishRide message to Kafka with data={} and traceId={}", request, traceId);

            kafkaTemplate.send(message).get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("Failed to send finish ride message to Kafka", ex);
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
