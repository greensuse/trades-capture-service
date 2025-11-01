package com.example.trades.until;

import com.example.trades.util.Masking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MaskingTest {

    @Test
    void maskNullAndBlankShouldReturnFourAsterisks() {
        assertEquals("****", Masking.maskAccount(null), "null should return ****");
        assertEquals("****", Masking.maskAccount(""), "empty string should return ****");
        assertEquals("****", Masking.maskAccount("   "), "blank string should return ****");
    }

    @Test
    void maskShortInputsShouldBePrefixedWithFourAsterisks() {
        assertEquals("****1", Masking.maskAccount("1"));
        assertEquals("****12", Masking.maskAccount("12"));
        assertEquals("****123", Masking.maskAccount("123"));
        assertEquals("****1234", Masking.maskAccount("1234"));
    }

    @Test
    void maskTypicalInputShouldKeepLastFourCharacters() {
        assertEquals("****5678", Masking.maskAccount("12345678"));
        assertEquals("****efgh", Masking.maskAccount("ab-cd-efgh"));
    }

    @Test
    void originalInputShouldNotBeModified() {
        String original = "1234567890";
        String copy = new String(original);
        String masked = Masking.maskAccount(original);

        assertEquals("****7890", masked);
        assertEquals(copy, original, "original input must remain unchanged");
    }
}
