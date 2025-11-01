// java
package com.example.trades.controller;

import com.example.trades.config.SecurityConfig;
import com.example.trades.model.InstructionRaw;
import com.example.trades.model.CanonicalTrade;
import com.example.trades.model.PlatformTrade;
import com.example.trades.util.TradeTransformer;
import com.example.trades.store.InMemoryStore;
import com.example.trades.service.KafkaPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeController.class)
@Import({TradeControllerTest.MockConfig.class, SecurityConfig.class})
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TradeTransformer transformer;

    @Autowired
    private InMemoryStore store;

    @Autowired
    private KafkaPublisher publisher;

    @Autowired
    private ObjectMapper objectMapper; // use Spring-managed ObjectMapper

    @TestConfiguration
    static class MockConfig {
        @Bean
        public TradeTransformer transformer() {
            return Mockito.mock(TradeTransformer.class);
        }

        @Bean
        public InMemoryStore store() {
            return Mockito.mock(InMemoryStore.class);
        }

        @Bean
        public KafkaPublisher publisher() {
            return Mockito.mock(KafkaPublisher.class);
        }
    }

    @Test
    void uploadCsvFile_shouldReturnOk_andInvokeStoreAndPublisher() throws Exception {
        String csv = "account_number,security_id,trade_type,quantity,price,trade_date\n" +
                "ACC123,SEC456,BUY,100,10.5,2025-10-31\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        CanonicalTrade mockCi = Mockito.mock(CanonicalTrade.class);
        Mockito.when(transformer.toCanonical(any(InstructionRaw.class))).thenReturn(mockCi);

        PlatformTrade mockPlatform = Mockito.mock(PlatformTrade.class);
        doReturn(mockPlatform).when(transformer).toPlatform(any(CanonicalTrade.class));

        mockMvc.perform(multipart("/api/v1/upload/file")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(transformer, times(1)).toCanonical(any(InstructionRaw.class));
        verify(store, times(1)).put(mockCi);
        verify(publisher, times(1)).publish(any(PlatformTrade.class));
    }

    @Test
    void uploadJsonFile_shouldReturnOk_andInvokeStoreAndPublisher() throws Exception {
        String jsonArray = "[" +
                "{\"accountNumber\":\"ACC123\",\"securityId\":\"SEC456\",\"tradeType\":\"BUY\",\"quantity\":\"100\",\"price\":\"10.5\",\"tradeDate\":\"2025-10-31\"}" +
                "]";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                jsonArray.getBytes(StandardCharsets.UTF_8)
        );

        CanonicalTrade mockCi = Mockito.mock(CanonicalTrade.class);
        Mockito.when(transformer.toCanonical(any(InstructionRaw.class))).thenReturn(mockCi);

        PlatformTrade mockPlatform = Mockito.mock(PlatformTrade.class);
        doReturn(mockPlatform).when(transformer).toPlatform(any(CanonicalTrade.class));

        mockMvc.perform(multipart("/api/v1/upload/file")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(transformer, times(2)).toCanonical(any(InstructionRaw.class));
        verify(store, times(1)).put(mockCi);
        verify(publisher, times(2)).publish(any(PlatformTrade.class));
    }

    @Test
    void publishInbound_shouldReturnAccepted_andInvokePublisher() throws Exception {
        // Build JSON payload manually to avoid Jackson classpath/access issues in the test runtime
        String payload = "{" +
                "\"accountNumber\":\"ACC123\"," +
                "\"securityId\":\"SEC456\"," +
                "\"tradeType\":\"BUY\"," +
                "\"quantity\":\"100\"," +
                "\"price\":\"10.5\"," +
                "\"tradeDate\":\"2025-10-31\"" +
                "}";

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/upload/publish-inbound")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isAccepted());

        verify(publisher, times(1)).publish(any(InstructionRaw.class));
    }
}
