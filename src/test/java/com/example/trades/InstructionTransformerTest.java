package com.example.trades;

import com.example.trades.model.CanonicalTrade;
import com.example.trades.model.InstructionRaw;
import com.example.trades.util.TradeTransformer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InstructionTransformerTest {
    @Test
    void testCanonicalTransform() {
        TradeTransformer t = new TradeTransformer();
        InstructionRaw raw = InstructionRaw.builder()
                .accountNumber("123456789")
                .securityId("aapl")
                .tradeType("Buy")
                .quantity("10")
                .price("180.50")
                .tradeDate("2025-10-01")
                .build();
        CanonicalTrade ci = t.toCanonical(raw);
        assertEquals("****6789", ci.getMaskedAccountNumber());
        assertEquals("AAPL", ci.getSecurityId());
        assertEquals("B", ci.getTradeTypeCode());
        assertEquals(10, ci.getQuantity());
        assertEquals(180.50, ci.getPrice(), 0.0001);
    }
}
