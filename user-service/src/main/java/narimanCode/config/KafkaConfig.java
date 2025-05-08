package narimanCode.config;

import narimanCode.dto.PasswordResetEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, PasswordResetEvent> kafkaTemplate(ProducerFactory<String, PasswordResetEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}