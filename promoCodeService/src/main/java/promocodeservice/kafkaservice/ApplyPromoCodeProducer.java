package promocodeservice.kafkaservice;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import promocodeservice.dto.ApplyPromocodeDTO;
import promocodeservice.exception.KafkaSendException;

import java.util.concurrent.TimeUnit;

@Service
public class ApplyPromoCodeProducer {

    private final KafkaTemplate<String, ApplyPromocodeDTO> kafkaTemplate;

    @Value("${kafka.topic.applyPromoCodeTopic}")
    private String applyPromoCodeTopic;

    public  ApplyPromoCodeProducer(KafkaTemplate<String, ApplyPromocodeDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendApplyPromoCodeRequest(ApplyPromocodeDTO request) {
        try {
            kafkaTemplate.send(applyPromoCodeTopic, request).get(10, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
