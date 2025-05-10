package promocodeservice.kafkaservice;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import promocodeservice.dto.CheckPromoCodeDTO;
import promocodeservice.service.PromoCodeService;

@Service
public class CheckPromoCodeConsumer {

    private static final Logger log = LoggerFactory.getLogger(CheckPromoCodeConsumer.class);
    private final PromoCodeService promoCodeService;

    @Autowired
    public CheckPromoCodeConsumer(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @KafkaListener(topics = "${kafka.topic.checkPromoCodeTopic}", containerFactory = "kafkaListenerContainerFactory")
    public void listenCancelRide(ConsumerRecord<String, CheckPromoCodeDTO> record) {

        log.info("Received CheckPromoCode message with payload={}", record.value());

        String traceId = record.headers().lastHeader("traceId") != null ? new String(record.headers().lastHeader("traceId").value()) : "N/A";
        String spanId = record.headers().lastHeader("spanId") != null ? new String(record.headers().lastHeader("spanId").value()) : "N/A";

        log.info("Received message with traceId={} and spanId={}", traceId, spanId);

        promoCodeService.checkPromoCode(record.value());
    }
}
