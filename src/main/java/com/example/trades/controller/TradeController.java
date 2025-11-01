package com.example.trades.controller;

import com.example.trades.model.CanonicalTrade;
import com.example.trades.model.InstructionRaw;
import com.example.trades.util.TradeTransformer;
import com.example.trades.store.InMemoryStore;
import com.example.trades.service.KafkaPublisher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class TradeController {
    private static final Logger log = LoggerFactory.getLogger(TradeController.class);

    private final TradeTransformer transformer;
    private final InMemoryStore store;
    private final KafkaPublisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping(path = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) return ResponseEntity.badRequest().body("Missing filename");
        String ext = StringUtils.getFilenameExtension(filename);
        if (ext == null) return ResponseEntity.badRequest().body("Unknown file type");

        List<CanonicalTrade> processed = new ArrayList<>();

        if (ext.equalsIgnoreCase("csv")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                 CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                         .withFirstRecordAsHeader()
                         .withIgnoreHeaderCase()
                         .withTrim())) {
                for (CSVRecord rec : parser) {
                    InstructionRaw raw = new InstructionRaw(
                            rec.get("account_number"),
                            rec.get("security_id"),
                            rec.get("trade_type"),
                            rec.get("quantity"),
                            rec.get("price"),
                            rec.get("trade_date")
                    );
                    CanonicalTrade ci = transformer.toCanonical(raw);
                    store.put(ci);
                    processed.add(ci);
                    publisher.publish(transformer.toPlatform(ci));
                }
            }
        } else if (ext.equalsIgnoreCase("json")) {
            List<InstructionRaw> raws = mapper.readValue(file.getInputStream(), new TypeReference<>() {});
            for (InstructionRaw raw : raws) {
                CanonicalTrade ci = transformer.toCanonical(raw);
                store.put(ci);
                processed.add(ci);
                publisher.publish(transformer.toPlatform(ci));
            }
        } else {
            return ResponseEntity.badRequest().body("Unsupported file type: " + ext);
        }

        return ResponseEntity.ok(processed);
    }

    @PostMapping(path = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadJson(@RequestBody List<InstructionRaw> raws) {
        List<String> ids = new ArrayList<>();
        for (InstructionRaw raw : raws) {
            CanonicalTrade ci = transformer.toCanonical(raw);
            store.put(ci);
            publisher.publish(transformer.toPlatform(ci));
            ids.add(ci.getId());
        }
        return ResponseEntity.accepted().body(ids);
    }

    @PostMapping("/publish-inbound")
    public ResponseEntity<Void> publishInbound(@RequestBody InstructionRaw instruction) {
        try {
            publisher.publish(instruction);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (Exception e) {
            log.error("Failed to publish inbound instruction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
