package com.example.trades.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class CanonicalTradeTest {

    @Test
    void testPojoContract() {
        try {
            Class<?> cls = Class.forName("com.example.trades.model.CanonicalTrade");
            Object instance = createInstance(cls);

            assertNotNull(instance, "Instance should not be null");
            assertEquals(instance, instance, "Object should be equal to itself");
            assertEquals(instance.hashCode(), instance.hashCode(), "hashCode should be stable");
            assertNotNull(instance.toString(), "toString() should not return null");
        } catch (ClassNotFoundException e) {
            fail("Class com.example.trades.model.CanonicalTrade not found");
        } catch (Exception e) {
            fail("Failed to construct or test CanonicalTrade: " + e.getMessage());
        }
    }

    private Object createInstance(Class<?> cls) throws Exception {
        for (Constructor<?> c : cls.getDeclaredConstructors()) {
            if (c.getParameterCount() == 0) {
                c.setAccessible(true);
                return c.newInstance();
            }
        }
        Constructor<?> c = cls.getDeclaredConstructors()[0];
        c.setAccessible(true);
        Class<?>[] pts = c.getParameterTypes();
        Object[] args = new Object[pts.length];
        for (int i = 0; i < pts.length; i++) {
            args[i] = defaultForType(pts[i]);
        }
        return c.newInstance(args);
    }

    private Object defaultForType(Class<?> t) {
        if (!t.isPrimitive()) return null;
        if (t == boolean.class) return false;
        if (t == byte.class) return (byte) 0;
        if (t == short.class) return (short) 0;
        if (t == char.class) return '\0';
        if (t == int.class) return 0;
        if (t == long.class) return 0L;
        if (t == float.class) return 0f;
        if (t == double.class) return 0d;
        return null;
    }
}
