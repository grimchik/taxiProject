package clientservice.kafkaservice;

import clientservice.dto.CheckPromoCodeDTO;
import clientservice.exception.KafkaSendException;
import io.micrometer.tracing.Tracer;
import jakarta.validation.Valid;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
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
public class CheckPromoCodeProducer {

    private static final Logger log = LoggerFactory.getLogger(CheckPromoCodeProducer.class);
    private final KafkaTemplate<String, CheckPromoCodeDTO> kafkaTemplate;
    private final Tracer tracer;

    @Value("${kafka.topic.checkPromoCodeTopic}")
    private String checkPromoCodeTopic;

    public CheckPromoCodeProducer(KafkaTemplate<String, CheckPromoCodeDTO> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendCheckPromoCodeRequest(@Valid CheckPromoCodeDTO request) {
        try {
            String traceId = tracer.currentTraceContext().context().traceId();

            Message<CheckPromoCodeDTO> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.TOPIC, checkPromoCodeTopic)
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