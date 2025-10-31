package com.example.trades.kafka;

import com.example.trades.model.CanonicalInstruction;
import com.example.trades.model.InstructionRaw;
import com.example.trades.model.PlatformInstruction;
import com.example.trades.service.InstructionTransformer;
import com.example.trades.store.InMemoryStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InboundListener {
    private static final Logger log = LoggerFactory.getLogger(InboundListener.class);
    private final InstructionTransformer transformer;
    private final OutboundPublisher publisher;
    private final InMemoryStore store;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "${app.kafka.topics.inbound}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            InstructionRaw raw = mapper.readValue(record.value(), InstructionRaw.class);
            CanonicalInstruction ci = transformer.toCanonical(raw);
            store.put(ci);
            PlatformInstruction pi = transformer.toPlatform(ci);
            publisher.publish(pi);
        } catch (Exception e) {
            log.error("Inbound processing failed at offset {}: {}", record.offset(), e.getMessage());
        }
    }
}
