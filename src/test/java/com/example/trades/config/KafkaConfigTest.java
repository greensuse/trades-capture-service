package com.example.trades.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

    @Test
    void kafkaConfig_createsProducerFactoryTemplateAndObjectMapper() {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(KafkaConfig.class)) {
            ProducerFactory<?, ?> producerFactory = ctx.getBean(ProducerFactory.class);
            assertNotNull(producerFactory, "ProducerFactory bean should not be null");

            KafkaTemplate<?, ?> kafkaTemplate = ctx.getBean(KafkaTemplate.class);
            assertNotNull(kafkaTemplate, "KafkaTemplate bean should not be null");

            ObjectMapper objectMapper = ctx.getBean(ObjectMapper.class);
            assertNotNull(objectMapper, "ObjectMapper bean should not be null");
        }
    }
}