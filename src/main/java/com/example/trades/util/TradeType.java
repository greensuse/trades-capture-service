package com.example.trades.util;

public final class TradeType {
    private TradeType() {}
    public static String normalize(String in) {
        if (in == null) return "U";
        String s = in.trim().toLowerCase();
        return switch (s) {
            case "b", "buy" -> "B";
            case "s", "sell" -> "S";
            default -> "U";
        };
    }
}
