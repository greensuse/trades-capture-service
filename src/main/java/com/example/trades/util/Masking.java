package com.example.trades.util;

public final class Masking {
    private Masking() {}
    public static String maskAccount(String acct) {
        if (acct == null || acct.isBlank()) return "****";
        String last4 = acct.length() <= 4 ? acct : acct.substring(acct.length()-4);
        return "****" + last4;
    }
}
