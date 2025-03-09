package clientservice.configuration;

import clientservice.dto.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.rideReplyTopic}")
    private String rideReplyTopic;

    @Value("${kafka.topic.getRideReplyTopic}")
    private String getRideReplyTopic;

    @Value("${kafka.topic.cancelRideReplyTopic}")
    private String cancelRideReplyTopic;

    @Bean
    public ProducerFactory<String, CreateRideRequestDTO> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, GetRidesRequestDTO> getProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, CanceledRideDTO> cancelProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConsumerFactory<String, RideWithIdDTO> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "ride-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "clientservice.dto");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(RideWithIdDTO.class, false));
    }

    @Bean
    public ConsumerFactory<String, RidePageResponse> getConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "get-ride-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "clientservice.dto");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(RidePageResponse.class, false));
    }

    @Bean
    public ConsumerFactory<String, RideWithIdDTO> cancelConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "cancel-ride-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "clientservice.dto");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(RideWithIdDTO.class, false));
    }

    @Bean
    public KafkaMessageListenerContainer<String, RideWithIdDTO> repliesContainer(
            @Qualifier("consumerFactory") ConsumerFactory<String, RideWithIdDTO> consumerFactory) {
        ContainerProperties containerProperties = new ContainerProperties(rideReplyTopic);
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public KafkaMessageListenerContainer<String, RidePageResponse> getRepliesContainer(
            @Qualifier("getConsumerFactory") ConsumerFactory<String, RidePageResponse> consumerFactory) {
        ContainerProperties containerProperties = new ContainerProperties(getRideReplyTopic);
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public KafkaMessageListenerContainer<String, RideWithIdDTO> cancelRepliesContainer(
            @Qualifier("cancelConsumerFactory") ConsumerFactory<String, RideWithIdDTO> consumerFactory) {
        ContainerProperties containerProperties = new ContainerProperties(cancelRideReplyTopic);
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, CreateRideRequestDTO, RideWithIdDTO> replyingKafkaTemplate(
            ProducerFactory<String, CreateRideRequestDTO> producerFactory,
            @Qualifier("repliesContainer") KafkaMessageListenerContainer<String, RideWithIdDTO> repliesContainer) {
        return new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
    }

    @Bean
    public ReplyingKafkaTemplate<String, GetRidesRequestDTO, RidePageResponse> getReplyingKafkaTemplate(
            ProducerFactory<String, GetRidesRequestDTO> producerFactory,
            @Qualifier("getRepliesContainer") KafkaMessageListenerContainer<String, RidePageResponse> getRepliesContainer) {
        return new ReplyingKafkaTemplate<>(producerFactory, getRepliesContainer);
    }

    @Bean
    public ReplyingKafkaTemplate<String, CanceledRideDTO, RideWithIdDTO> cancelReplyingKafkaTemplate(
            ProducerFactory<String, CanceledRideDTO> producerFactory,
            @Qualifier("cancelRepliesContainer") KafkaMessageListenerContainer<String, RideWithIdDTO> cancelRepliesContainer) {
        return new ReplyingKafkaTemplate<>(producerFactory, cancelRepliesContainer);
    }
}
