package rideservice.kafkaservice.producer;

import rideservice.dto.CreatePaymentDTO;
import rideservice.exception.KafkaSendException;
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
public class CreatePaymentProducer {

    private static final Logger log = LoggerFactory.getLogger(CreatePaymentProducer.class);
    private final KafkaTemplate<String, CreatePaymentDTO> kafkaTemplate;
    private final Tracer tracer;

    @Value("${kafka.topic.createPaymentTopic}")
    private String createPaymentTopic;

    public CreatePaymentProducer(KafkaTemplate<String, CreatePaymentDTO> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendCreatePaymentRequest(@Valid CreatePaymentDTO request) {
        try {
            String traceId = tracer.currentTraceContext().context().traceId();

            Message<CreatePaymentDTO> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.TOPIC, createPaymentTopic)
                    .setHeader("traceId", traceId)
                    .build();

            log.info("Sending CreatePayment message to Kafka with payload={} and traceId={}", request, traceId);

            kafkaTemplate.send(message).get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("Failed to send CreatePayment message to Kafka", ex);
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}