package com.example.trades.store;

import com.example.trades.model.CanonicalTrade;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStore {
    private final Map<String, CanonicalTrade> store = new ConcurrentHashMap<>();
    public void put(CanonicalTrade ci) {
        if (ci != null && ci.getId() != null) store.put(ci.getId(), ci);
    }
    public CanonicalTrade get(String id) { return store.get(id); }
    public int size() { return store.size(); }
}
