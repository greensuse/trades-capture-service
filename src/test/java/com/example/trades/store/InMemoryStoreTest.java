package com.example.trades.store;

import com.example.trades.model.CanonicalTrade;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryStoreTest {

    @Test
    void putAndGetShouldStoreAndReturnTrade() {
        InMemoryStore store = new InMemoryStore();

        CanonicalTrade trade = Mockito.mock(CanonicalTrade.class);
        Mockito.when(trade.getId()).thenReturn("t1");

        store.put(trade);

        assertEquals(1, store.size(), "store size should be 1 after putting one trade");
        assertSame(trade, store.get("t1"), "retrieved trade should be the same instance that was put");
    }

    @Test
    void putNullShouldBeIgnored() {
        InMemoryStore store = new InMemoryStore();

        store.put(null);

        assertEquals(0, store.size(), "putting null should not increase store size");
    }

    @Test
    void putTradeWithNullIdShouldBeIgnored() {
        InMemoryStore store = new InMemoryStore();

        CanonicalTrade trade = Mockito.mock(CanonicalTrade.class);
        Mockito.when(trade.getId()).thenReturn(null);

        store.put(trade);

        assertEquals(0, store.size(), "trade with null id should not be stored");
    }

    @Test
    void putWithSameIdShouldOverwriteExisting() {
        InMemoryStore store = new InMemoryStore();

        CanonicalTrade first = Mockito.mock(CanonicalTrade.class);
        Mockito.when(first.getId()).thenReturn("same");

        CanonicalTrade second = Mockito.mock(CanonicalTrade.class);
        Mockito.when(second.getId()).thenReturn("same");

        store.put(first);
        store.put(second);

        assertEquals(1, store.size(), "storing two trades with same id should result in size 1");
        assertSame(second, store.get("same"), "latest trade with same id should overwrite the previous one");
    }
}
