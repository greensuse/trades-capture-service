package com.example.trades.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic inboundTopic(@Value("${app.kafka.topics.inbound}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
    @Bean
    public NewTopic outboundTopic(@Value("${app.kafka.topics.outbound}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

}
