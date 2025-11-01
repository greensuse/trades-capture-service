package com.example.trades.model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CanonicalTrade {
    private String maskedAccountNumber;
    private String securityId;
    private String tradeTypeCode;
    private long quantity;
    private double price;
    private String tradeDate;
    private String id;
}
