package paymentservice.kafkaservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import paymentservice.dto.CreatePaymentDTO;
import paymentservice.service.PaymentService;

@Service
public class CreatePaymentConsumer {
    private final PaymentService paymentService;

    @Autowired
    public CreatePaymentConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${kafka.topic.createPaymentTopic}", containerFactory = "createPaymentKafkaListenerContainerFactory")
    public void listenCancelRide(CreatePaymentDTO message) {
        paymentService.createPaymentByRide(message);
    }
}
