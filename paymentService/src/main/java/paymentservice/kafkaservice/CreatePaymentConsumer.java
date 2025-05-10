package paymentservice.kafkaservice;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import paymentservice.dto.CreatePaymentDTO;
import paymentservice.service.PaymentService;

@Service
public class CreatePaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(CreatePaymentConsumer.class);
    private final PaymentService paymentService;

    public CreatePaymentConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${kafka.topic.createPaymentTopic}", containerFactory = "createPaymentKafkaListenerContainerFactory")
    public void listenCancelRide(ConsumerRecord<String, CreatePaymentDTO> record) {

        log.info("Received CreatePayment message with payload={}", record.value());

        String traceId = record.headers().lastHeader("traceId") != null ? new String(record.headers().lastHeader("traceId").value()) : "N/A";
        String spanId = record.headers().lastHeader("spanId") != null ? new String(record.headers().lastHeader("spanId").value()) : "N/A";

        log.info("Received message with traceId={} and spanId={}", traceId, spanId);

        paymentService.createPaymentByRide(record.value());

    }
}
