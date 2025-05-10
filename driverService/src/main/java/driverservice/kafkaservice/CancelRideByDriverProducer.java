package driverservice.kafkaservice;

import driverservice.dto.CanceledRideByDriverDTO;
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
public class CancelRideByDriverProducer {

    private static final Logger log = LoggerFactory.getLogger(CancelRideByDriverProducer.class);
    private final KafkaTemplate<String, CanceledRideByDriverDTO> kafkaTemplate;
    private final Tracer tracer;

    @Value("${kafka.topic.rideCancelByDriverTopic}")
    private String rideCancelByDriverTopic;

    public CancelRideByDriverProducer(KafkaTemplate<String, CanceledRideByDriverDTO> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendCancelRequest(@Valid CanceledRideByDriverDTO request) {
        try {
            String traceId = tracer.currentTraceContext().context().traceId();

            Message<CanceledRideByDriverDTO> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.TOPIC, rideCancelByDriverTopic)
                    .setHeader("traceId", traceId)
                    .build();

            log.info("Sending ApplyPromoCode message to Kafka with data={} and traceId={}", request, traceId);

            kafkaTemplate.send(message).get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("Failed to send complete ride message to Kafka", ex);
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
