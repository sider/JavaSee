package com.github.sider.javasee;

import com.github.sider.javasee.ast.AST;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class RuleTest {
    @AllArgsConstructor
    @Getter
    @ToString
    public static class Entry<K, V> {
        public final K k;
        public final V v;
    }

    private static <K, V> Entry<K, V> kv(K k, V v) {
        return new Entry<K, V>(k, v);
    }

    private static <K, V> Map<K, V> mapOf(Entry<K, V>... entries) {
        Map<K, V> result = new HashMap<>();
        for(var e : entries) {
            result.put(e.k, e.v);
        }
        return result;
    }

    @Test
    public void testLoadRule1() {
        var rule = Rule.load(
                mapOf(
                        kv("id", "foo.bar.baz"),
                        kv("pattern", "_"),
                        kv("message", "message1")
                )
        );

        assertEquals("foo.bar.baz", rule.id);
        assertEquals("message1", rule.message);
        assertTrue(rule.patterns.get(0) instanceof AST.Wildcard);
        assertEquals(new ArrayList<>(), rule.justifications);

    }

    @Test
    public void testLoadRule2() {
        var rule = Rule.load(
                mapOf(
                        kv("id", "foo.bar.baz"),
                        kv("pattern", List.of("foo.bar", "_")),
                        kv("message", "message1"),
                        kv("justification", List.of("some", "message"))
                )
        );

        assertEquals("foo.bar.baz", rule.id);
        assertEquals("message1", rule.message);
        assertTrue(rule.patterns.get(0) instanceof AST.FieldSelection);
        var receiver = (AST.ID)((AST.FieldSelection)(rule.patterns.get(0))).receiver;
        var name = ((AST.FieldSelection)(rule.patterns.get(0))).name;
        assertEquals("foo", receiver.name);
        assertEquals("bar", name);
        assertTrue(rule.patterns.get(1) instanceof AST.Wildcard);
        assertEquals(List.of("some", "message"), rule.justifications);
    }

    @Test
    public void testLoadRule3() {
        var rule = Rule.load(
                mapOf(
                        kv("id", "foo.bar.baz"),
                        kv("pattern", List.of("100", "_")),
                        kv("message", "message1"),
                        kv("justification", List.of("some", "message"))
                )
        );

        assertEquals("foo.bar.baz", rule.id);
        assertEquals("message1", rule.message);
        assertTrue(rule.patterns.get(0) instanceof AST.IntLiteral);
        assertTrue(rule.patterns.get(1) instanceof AST.Wildcard);
        assertEquals(List.of("some", "message"), rule.justifications);
    }
}
