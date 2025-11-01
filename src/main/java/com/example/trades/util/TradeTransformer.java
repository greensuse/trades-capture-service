package com.example.trades.util;

import com.example.trades.model.CanonicalTrade;
import com.example.trades.model.InstructionRaw;
import com.example.trades.model.PlatformTrade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Service
public class TradeTransformer {
    public CanonicalTrade toCanonical(InstructionRaw raw) {
        String sec = raw.getSecurityId() == null ? "" : raw.getSecurityId().trim().toUpperCase(Locale.ROOT);
        if (sec.isEmpty() || !sec.matches("^[A-Z0-9._-]{1,24}$")) {
            throw new IllegalArgumentException("Invalid security_id");
        }
        String side = TradeType.normalize(raw.getTradeType());
        String date = (raw.getTradeDate() == null || raw.getTradeDate().isBlank())
                ? LocalDate.now().toString() : raw.getTradeDate().trim();
        long qty = Long.parseLong(raw.getQuantity().trim());
        double px = Double.parseDouble(raw.getPrice().trim());

        return CanonicalTrade.builder()
                .id(UUID.randomUUID().toString())
                .maskedAccountNumber(Masking.maskAccount(raw.getAccountNumber()))
                .securityId(sec)
                .tradeTypeCode(side)
                .quantity(qty)
                .price(px)
                .tradeDate(date)
                .build();
    }
    public PlatformTrade toPlatform(CanonicalTrade c) {
        // Convert monetary value to minor units (cents) and round to long
        long amountInCents = Math.round(c.getQuantity() * c.getPrice() * 100.0);

        // Use canonical id as platform_id, masked account is safe to pass (PlatformTrade masks again idempotently)
        String platformId = c.getId();
        String account = c.getMaskedAccountNumber();
        String security = c.getSecurityId();
        String type = c.getTradeTypeCode();
        String timestamp = c.getTradeDate();

        return PlatformTrade.of(platformId, account, security, type, amountInCents, timestamp);
    }
}
