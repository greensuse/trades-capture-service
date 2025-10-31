package com.example.trades.model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CanonicalInstruction {
    private String maskedAccountNumber;
    private String securityId;
    private String tradeTypeCode;
    private long quantity;
    private double price;
    private String tradeDate;
    private String id;
}
