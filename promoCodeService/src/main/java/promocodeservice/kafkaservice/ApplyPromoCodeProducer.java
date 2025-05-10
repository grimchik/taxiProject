package promocodeservice.kafkaservice;

import promocodeservice.dto.ApplyPromocodeDTO;
import promocodeservice.exception.KafkaSendException;
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
public class ApplyPromoCodeProducer {

    private static final Logger log = LoggerFactory.getLogger(ApplyPromoCodeProducer.class);
    private final KafkaTemplate<String, ApplyPromocodeDTO> kafkaTemplate;
    private final Tracer tracer;

    @Value("${kafka.topic.applyPromoCodeTopic}")
    private String applyPromoCodeTopic;

    public ApplyPromoCodeProducer(KafkaTemplate<String, ApplyPromocodeDTO> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendApplyPromoCodeRequest(@Valid ApplyPromocodeDTO request) {
        try {
            String traceId = tracer.currentTraceContext().context().traceId();

            Message<ApplyPromocodeDTO> message = MessageBuilder
                    .withPayload(request)
                    .setHeader(KafkaHeaders.TOPIC, applyPromoCodeTopic)
                    .setHeader("traceId", traceId)
                    .build();

            log.info("Sending ApplyPromoCode message to Kafka with data={} and traceId={}", request, traceId);

            kafkaTemplate.send(message).get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("Failed to send ApplyPromoCode message to Kafka", ex);
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
