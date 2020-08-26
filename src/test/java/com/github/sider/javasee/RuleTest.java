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
                        kv("justification", List.of("some", "message")),
                        kv("tests",
                                mapOf(kv("match", List.of("1", "2", "1+2")),
                                        kv("unmatch", "foo()")))
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
        assertEquals(List.of("1", "2", "1+2"), rule.matchExamples);
        assertEquals(List.of("foo()"), rule.unmatchExamples);
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

    @Test void testLoadInvalidRule1() {
        assertThrows(Exceptions.UnknownKeysException.class, () -> {
            Rule.load(
                    mapOf(
                            kv("id", "foo.bar.baz"),
                            kv("pattern", List.of("foo.bar", "_")),
                            kv("hoge", "foo")
                    )
            );
        });
    }

    @Test void testLoadInvalidRule2() {
        assertThrows(Exceptions.MissingKeyException.class, () -> {
            Rule.load(
                    mapOf(
                            kv("pattern", List.of("foo.bar", "_")),
                            kv("message", "hoge")
                    )
            );
        });
    }

    @Test void testLoadInvalidRule3() {
        assertThrows(Exceptions.MissingKeyException.class, () -> {
            Rule.load(
                    mapOf(
                            kv("id", "foo.bar.baz"),
                            kv("pattern", List.of("foo.bar", "_"))
                    )
            );
        });
    }

    @Test void testLoadInvalidRule4() {
        assertThrows(Exceptions.MissingKeyException.class, () -> {
            Rule.load(
                    mapOf(
                            kv("id", "foo.bar.baz"),
                            kv("message", "hoge")
                    )
            );
        });
    }

    @Test
    public void testLoadInvalidRule5() {
        assertThrows(Exceptions.InvalidTypeException.class, () -> {
            Rule.load(
                    mapOf(
                            kv("id", "foo.bar.baz"),
                            kv("pattern", kv("bar", "baz")),
                            kv("message", "message1"),
                            kv("justification", List.of("some", "message")),
                            kv("tests",
                                    mapOf(kv("match", List.of("1", "2", "1+2")),
                                            kv("unmatch", "foo()")))
                    )
            );
        });
    }
}
