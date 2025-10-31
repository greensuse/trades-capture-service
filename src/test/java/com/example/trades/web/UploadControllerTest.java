// java
package com.example.trades.web;

import com.example.trades.model.InstructionRaw;
import com.example.trades.model.CanonicalInstruction;
import com.example.trades.service.InstructionTransformer;
import com.example.trades.store.InMemoryStore;
import com.example.trades.kafka.OutboundPublisher;
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

@WebMvcTest(UploadController.class)
@Import(UploadControllerTest.MockConfig.class)
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InstructionTransformer transformer;

    @Autowired
    private InMemoryStore store;

    @Autowired
    private OutboundPublisher publisher;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public InstructionTransformer transformer() {
            return Mockito.mock(InstructionTransformer.class);
        }

        @Bean
        public InMemoryStore store() {
            return Mockito.mock(InMemoryStore.class);
        }

        @Bean
        public OutboundPublisher publisher() {
            return Mockito.mock(OutboundPublisher.class);
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

        CanonicalInstruction mockCi = Mockito.mock(CanonicalInstruction.class);
        Mockito.when(transformer.toCanonical(any(InstructionRaw.class))).thenReturn(mockCi);

        // Use doReturn(...) for stubbing when thenReturn is not resolvable
        doReturn(new Object()).when(transformer).toPlatform(any(CanonicalInstruction.class));
        // If toPlatform is void, replace the line above with:
        // Mockito.doNothing().when(transformer).toPlatform(any(CanonicalInstruction.class));

        mockMvc.perform(multipart("/api/v1/upload/file")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(transformer, times(1)).toCanonical(any(InstructionRaw.class));
        verify(store, times(1)).put(mockCi);
        verify(publisher, times(1)).publish(any());
    }

    @Test
    void uploadJsonFile_shouldReturnOk_andInvokeStoreAndPublisher() throws Exception {
        String jsonArray = "[" +
                "{\"account_number\":\"ACC123\",\"security_id\":\"SEC456\",\"trade_type\":\"BUY\",\"quantity\":\"100\",\"price\":\"10.5\",\"trade_date\":\"2025-10-31\"}" +
                "]";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                jsonArray.getBytes(StandardCharsets.UTF_8)
        );

        CanonicalInstruction mockCi = Mockito.mock(CanonicalInstruction.class);
        Mockito.when(transformer.toCanonical(any(InstructionRaw.class))).thenReturn(mockCi);

        doReturn(new Object()).when(transformer).toPlatform(any(CanonicalInstruction.class));
        // Or use Mockito.doNothing() if the method is void:
        // Mockito.doNothing().when(transformer).toPlatform(any(CanonicalInstruction.class));

        mockMvc.perform(multipart("/api/v1/upload/file")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(transformer, times(1)).toCanonical(any(InstructionRaw.class));
        verify(store, times(1)).put(mockCi);
        verify(publisher, times(1)).publish(any());
    }
}
