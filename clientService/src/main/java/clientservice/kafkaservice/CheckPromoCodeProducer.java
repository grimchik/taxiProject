package clientservice.kafkaservice;

import clientservice.dto.CheckPromoCodeDTO;
import clientservice.exception.KafkaSendException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CheckPromoCodeProducer
{
    private final KafkaTemplate<String, CheckPromoCodeDTO> kafkaTemplate;

    @Value("${kafka.topic.checkPromoCodeTopic}")
    private String checkPromoCodeTopic;

    public CheckPromoCodeProducer(KafkaTemplate<String, CheckPromoCodeDTO> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCheckPromoCodeRequest(@Valid CheckPromoCodeDTO request) {
        try {
            kafkaTemplate.send(checkPromoCodeTopic, request).get(10, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            throw new KafkaSendException("Failed to send message to Kafka", ex);
        }
    }
}
