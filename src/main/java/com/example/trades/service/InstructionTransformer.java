package com.example.trades.service;

import com.example.trades.model.CanonicalInstruction;
import com.example.trades.model.InstructionRaw;
import com.example.trades.model.PlatformInstruction;
import com.example.trades.util.Masking;
import com.example.trades.util.TradeType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Service
public class InstructionTransformer {
    public CanonicalInstruction toCanonical(InstructionRaw raw) {
        String sec = raw.getSecurityId() == null ? "" : raw.getSecurityId().trim().toUpperCase(Locale.ROOT);
        if (sec.isEmpty() || !sec.matches("^[A-Z0-9._-]{1,24}$")) {
            throw new IllegalArgumentException("Invalid security_id");
        }
        String side = TradeType.normalize(raw.getTradeType());
        String date = (raw.getTradeDate() == null || raw.getTradeDate().isBlank())
                ? LocalDate.now().toString() : raw.getTradeDate().trim();
        long qty = Long.parseLong(raw.getQuantity().trim());
        double px = Double.parseDouble(raw.getPrice().trim());

        return CanonicalInstruction.builder()
                .id(UUID.randomUUID().toString())
                .maskedAccountNumber(Masking.maskAccount(raw.getAccountNumber()))
                .securityId(sec)
                .tradeTypeCode(side)
                .quantity(qty)
                .price(px)
                .tradeDate(date)
                .build();
    }
    public PlatformInstruction toPlatform(CanonicalInstruction c) {
        return PlatformInstruction.builder()
                .acct_ref(c.getMaskedAccountNumber())
                .sec_id(c.getSecurityId())
                .side(c.getTradeTypeCode())
                .qty(c.getQuantity())
                .px(c.getPrice())
                .ts(c.getTradeDate())
                .sourceId(c.getId())
                .build();
    }
}
