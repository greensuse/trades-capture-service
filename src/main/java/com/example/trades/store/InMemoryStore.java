package com.example.trades.store;

import com.example.trades.model.CanonicalInstruction;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStore {
    private final Map<String, CanonicalInstruction> store = new ConcurrentHashMap<>();
    public void put(CanonicalInstruction ci) {
        if (ci != null && ci.getId() != null) store.put(ci.getId(), ci);
    }
    public CanonicalInstruction get(String id) { return store.get(id); }
    public int size() { return store.size(); }
}
