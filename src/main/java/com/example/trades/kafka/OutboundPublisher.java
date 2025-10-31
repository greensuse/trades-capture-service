package com.example.trades.kafka;

import com.example.trades.model.PlatformInstruction;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OutboundPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboundPublisher.class);
    private final KafkaTemplate<String, String> template;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.kafka.topics.outbound}")
    private String outboundTopic;

    public void publish(PlatformInstruction pi) {
        try {
            String payload = objectMapper.writeValueAsString(pi);
            CompletableFuture<SendResult<String, String>> future = template.send(outboundTopic, pi.getSourceId(), payload);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish outbound key={} topic={}", pi.getSourceId(), outboundTopic, ex);
                    CompletableFuture<RecordMetadata> cf = new CompletableFuture<>();
                    cf.completeExceptionally(ex);
                } else {
                    var md = result.getRecordMetadata();
                    log.info("Successfully sent outbound key={} topic={} partition={} offset={}", pi.getSourceId(), md.topic(), md.partition(), md.offset());
                }
            });
        } catch (Exception e) {
            CompletableFuture<RecordMetadata> cf = new CompletableFuture<>();
            cf.completeExceptionally(e);
        }
    }
}
