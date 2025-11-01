package com.example.trades.service;

import com.example.trades.model.CanonicalTrade;
import com.example.trades.model.InstructionRaw;
import com.example.trades.model.PlatformTrade;
import com.example.trades.util.TradeTransformer;
import com.example.trades.store.InMemoryStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaListenerService.class);
    private final TradeTransformer transformer;
    private final KafkaPublisher publisher;
    private final InMemoryStore store;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "${app.kafka.topics.inbound}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            InstructionRaw raw = mapper.readValue(record.value(), InstructionRaw.class);
            CanonicalTrade ci = transformer.toCanonical(raw);
            store.put(ci);
            PlatformTrade pi = transformer.toPlatform(ci);
            publisher.publish(pi);
        } catch (Exception e) {
            log.error("Inbound processing failed at offset {}: {}", record.offset(), e.getMessage());
        }
    }
}
