package promocodeservice.kafkaservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import promocodeservice.dto.CheckPromoCodeDTO;
import promocodeservice.service.PromoCodeService;

@Service
public class CheckPromoCodeConsumer {
    private final PromoCodeService promoCodeService;

    @Autowired
    public  CheckPromoCodeConsumer(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @KafkaListener(topics = "${kafka.topic.checkPromoCodeTopic}", containerFactory = "kafkaListenerContainerFactory")
    public void listenCancelRide(CheckPromoCodeDTO message) {
        promoCodeService.checkPromoCode(message);
    }

}
