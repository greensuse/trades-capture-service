package com.example.trades.model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InstructionRaw {
    private String accountNumber;
    private String securityId;
    private String tradeType;
    private String quantity;
    private String price;
    private String tradeDate;
}
