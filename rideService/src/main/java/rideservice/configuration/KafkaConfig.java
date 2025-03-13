package rideservice.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import rideservice.dto.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.groupId}")
    private String groupId;
    @Value("${spring.kafka.applyGroupId}")
    private String applyGroupId;

    @Bean
    public ConsumerFactory<String, CanceledRideDTO> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(CanceledRideDTO.class,false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CanceledRideDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CanceledRideDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CanceledRideByDriverDTO> driverCancelconsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(CanceledRideByDriverDTO.class,false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CanceledRideByDriverDTO> driverCancelKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CanceledRideByDriverDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(driverCancelconsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, RideInProgressDTO> rideInProgressConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(RideInProgressDTO.class,false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RideInProgressDTO> rideInProgressKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RideInProgressDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(rideInProgressConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ApplyPromocodeDTO> applyPromocodeConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, applyGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(ApplyPromocodeDTO.class,false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ApplyPromocodeDTO> kafkaApplyPromoCodeListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ApplyPromocodeDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(applyPromocodeConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, FinishRideDTO> finishRideConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, applyGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(FinishRideDTO.class,false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FinishRideDTO> kafkaFinishRideListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FinishRideDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(finishRideConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CompleteRideDTO> completeRideConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, applyGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(CompleteRideDTO.class,false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CompleteRideDTO> kafkaCompleteRideListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CompleteRideDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(completeRideConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, CreatePaymentDTO> createPaymentProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, CreatePaymentDTO> finishRideKafkaTemplate(ProducerFactory<String, CreatePaymentDTO> createPaymentProducerFactory) {
        return new KafkaTemplate<>(createPaymentProducerFactory);
    }

}