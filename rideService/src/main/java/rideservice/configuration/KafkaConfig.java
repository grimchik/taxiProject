package rideservice.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import rideservice.dto.*;
import rideservice.entity.Ride;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.groupId}")
    private String kafkaGroup;

    @Value("${spring.kafka.getGroupId}")
    private String getKafkaGroup;

    @Value("${spring.kafka.cancelGroupId}")
    private String cancelKafkaGroup;

    @Bean
    public ProducerFactory<String, RideWithIdDTO> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, RidePageResponse> getProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, RideWithIdDTO> cancelProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, RideWithIdDTO> rideKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, RidePageResponse> getRideKafkaTemplate() {
        return new KafkaTemplate<>(getProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, RideWithIdDTO> cancelRideKafkaTemplate() {
        return new KafkaTemplate<>(cancelProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, RideDTO> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroup);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        JsonDeserializer<RideDTO> jsonDeserializer = new JsonDeserializer<>(RideDTO.class, false);
        jsonDeserializer.addTrustedPackages("rideservice.dto");

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConsumerFactory<String, GetRidesRequestDTO> getConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, getKafkaGroup);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        JsonDeserializer<GetRidesRequestDTO> jsonDeserializer = new JsonDeserializer<>(GetRidesRequestDTO.class, false);
        jsonDeserializer.addTrustedPackages("rideservice.dto");

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConsumerFactory<String, CanceledRideDTO> cancelConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, cancelKafkaGroup);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        JsonDeserializer<CanceledRideDTO> jsonDeserializer = new JsonDeserializer<>(CanceledRideDTO.class, false);
        jsonDeserializer.addTrustedPackages("rideservice.dto");

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RideDTO> kafkaListenerContainerFactory(
            @Qualifier("rideKafkaTemplate") KafkaTemplate<String, RideWithIdDTO> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, RideDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GetRidesRequestDTO> getKafkaListenerContainerFactory(
            @Qualifier("getRideKafkaTemplate") KafkaTemplate<String, RidePageResponse> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, GetRidesRequestDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(getConsumerFactory());
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CanceledRideDTO> cancelKafkaListenerContainerFactory(
            @Qualifier("cancelRideKafkaTemplate") KafkaTemplate<String, RideWithIdDTO> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, CanceledRideDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cancelConsumerFactory());
        factory.setReplyTemplate(kafkaTemplate);
        return factory;
    }
}