package com.example.trades.service;

import com.example.trades.model.InstructionRaw;
import com.example.trades.model.PlatformTrade;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);
    private final KafkaTemplate<String, String> template;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.kafka.topics.outbound}")
    private String outboundTopic;

    @Value("${app.kafka.topics.inbound}")
    private String inboundTopic;

    public void publish(PlatformTrade pi) {
        try {
            String payload = objectMapper.writeValueAsString(pi);
            String key = (pi != null) ? pi.getPlatformId() : null;

            CompletableFuture<SendResult<String, String>> future = template.send(outboundTopic, key, payload);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish outbound platformId={} topic={}", key, outboundTopic, ex);
                } else {
                    var md = result.getRecordMetadata();
                    log.info("Successfully sent outbound platformId={} topic={} partition={} offset={}",
                            key, md.topic(), md.partition(), md.offset());
                }
            });
        } catch (Exception e) {
            log.error("Failed to serialize/publish outbound PlatformTrade: {}", e.getMessage(), e);
        }
    }

    public void publish(InstructionRaw instruction) {
        try {
            String payload = objectMapper.writeValueAsString(instruction);
            CompletableFuture<SendResult<String, String>> future = template.send(inboundTopic, payload);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish inbound message to {}: {}", inboundTopic, ex.getMessage());
                } else {
                    var md = result.getRecordMetadata();
                    log.info("Published inbound message to {} partition={} offset={}",
                            inboundTopic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            CompletableFuture<RecordMetadata> cf = new CompletableFuture<>();
            cf.completeExceptionally(e);
        }
    }
}
