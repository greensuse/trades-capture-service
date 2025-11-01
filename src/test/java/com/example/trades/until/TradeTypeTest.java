package com.example.trades.until;

import com.example.trades.util.TradeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TradeTypeTest {

    @Test
    void nullInputReturnsU() {
        assertEquals("U", TradeType.normalize(null));
    }

    @Test
    void buyVariantsNormalizeToB() {
        assertEquals("B", TradeType.normalize("b"));
        assertEquals("B", TradeType.normalize("B"));
        assertEquals("B", TradeType.normalize("buy"));
        assertEquals("B", TradeType.normalize("BUY"));
        assertEquals("B", TradeType.normalize("  buy  "));
        assertEquals("B", TradeType.normalize(" BuY "));
    }

    @Test
    void sellVariantsNormalizeToS() {
        assertEquals("S", TradeType.normalize("s"));
        assertEquals("S", TradeType.normalize("S"));
        assertEquals("S", TradeType.normalize("sell"));
        assertEquals("S", TradeType.normalize("SELL"));
        assertEquals("S", TradeType.normalize("  sell  "));
        assertEquals("S", TradeType.normalize(" SeLl "));
    }

    @Test
    void unknownValuesReturnU() {
        assertEquals("U", TradeType.normalize(""));
        assertEquals("U", TradeType.normalize(" "));
        assertEquals("U", TradeType.normalize("unknown"));
        assertEquals("U", TradeType.normalize("x"));
        assertEquals("U", TradeType.normalize("bu y"));
    }
}
