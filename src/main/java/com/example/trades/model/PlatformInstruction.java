package com.example.trades.model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlatformInstruction {
    private String acct_ref;
    private String sec_id;
    private String side;
    private long qty;
    private double px;
    private String ts;
    private String sourceId;
}
