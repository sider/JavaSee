package com.github.sider.java_see;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/*
class RuleTest < Minitest::Test
        def test_load_rule_before_and_after_examples
        rule = Rule.load(
        "id" => "foo.bar.baz",
        "pattern" => ["@", "_"],
        "message" => "message1",
        "tags" => ["tag1", "tag2"],
        "before" => ["foo", "bar"],
        "after" => ["baz", "a"],
        "justification" => ["some", "message"]
        )

        assert_equal "foo.bar.baz", rule.id
        assert_equal ["message1"], rule.messages
        assert_equal [E::Ivar.new(name: nil), E::Any.new], rule.patterns.map(&:expr)
        assert_equal Set.new(["tag1", "tag2"]), rule.tags
        assert_equal [], rule.examples
        assert_equal ["foo", "bar"], rule.before_examples
        assert_equal ["baz", "a"], rule.after_examples
        assert_equal ["some", "message"], rule.justifications
        end

        def test_load_rule_raises_on_pattern_syntax_error
        exn = assert_raises Rule::PatternSyntaxError do
        Rule.load("id" => "id1", "pattern" => "syntax error")
        end

        assert_match(/Pattern syntax error: rule=id1, index=0, pattern=syntax error, where={}:/, exn.message)
        end

        def test_load_rule_raises_without_id
        exn = assert_raises Rule::InvalidRuleHashError do
        Rule.load("pattern" => "_", "message" => "message1")
        end

        assert_equal "id is missing", exn.message
        end

        def test_load_rule_raises_without_pattern
        exn = assert_raises Rule::InvalidRuleHashError do
        Rule.load("id" => "id1", "message" => "hello world")
        end

        assert_equal "pattern is missing", exn.message
        end

        def test_load_rule_raises_without_message
        exn = assert_raises Rule::InvalidRuleHashError do
        Rule.load("id" => "id1", "pattern" => "foobar")
        end

        assert_equal "message is missing", exn.message
        end

        def test_load_including_pattern_with_where_clause
        rule = Rule.load("id" => "id1", "message" => "message", "pattern" => { 'subject' => "'g()'", 'where' => { 'g' => ["foo", "/bar/"] } })
        assert_equal 1, rule.patterns.size

        pattern = rule.patterns.first
        assert_equal ["foo", /bar/], pattern.expr.name
        end

        def test_load_rule_raises_exception_on_invalid_example
        assert_raises Rule::InvalidRuleHashError do
        Rule.load("id" => "id1", "message" => "message", "pattern" => { 'subject' => "'g()'", 'where' => { 'g' => ["foo", "/bar/"] } }, "examples" => [{}])
        end
        end

        def test_translate_where
        w = YAML.load(<<-YAML)
        - foo
        - /bar/
        - :baz
        - 1
        - 2.0
        YAML

        assert_equal ["foo", /bar/, :baz, 1, 2.0], Rule.translate_where(w)
        end
        end
*/
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
        assertEquals(List.of("message1"), rule.messages);
        assertTrue(rule.patterns.get(0) instanceof AST.Wildcard);
        assertEquals(new HashSet<>(), rule.tags);
        assertEquals(new ArrayList<>(), rule.examples);
        assertEquals(new ArrayList<>(), rule.justifications);

    }

    @Test
    public void testLoadRule2() {
        var rule = Rule.load(
                mapOf(
                        kv("id", "foo.bar.baz"),
                        kv("pattern", List.of("foo.bar", "_")),
                        kv("message", "message1"),
                        kv("tags", List.of("tag1", "tag2")),
                        kv("examples", List.of(
                                mapOf(kv("before", "foo"), kv("after", "bar")),
                                mapOf(kv("before", "foo")),
                                mapOf(kv("after", "bar"))
                        )),
                        kv("justification", List.of("some", "message"))
                )
        );

        assertEquals("foo.bar.baz", rule.id);
        assertEquals(List.of("message1"), rule.messages);
        assertTrue(rule.patterns.get(0) instanceof AST.FieldSelection);
        var receiver = (AST.ID)((AST.FieldSelection)(rule.patterns.get(0))).receiver;
        var name = ((AST.FieldSelection)(rule.patterns.get(0))).name;
        assertEquals("foo", receiver.name);
        assertEquals("bar", name);
        assertTrue(rule.patterns.get(1) instanceof AST.Wildcard);
        assertEquals(Set.of("tag1", "tag2"), rule.tags);
        assertEquals(
                List.of(
                        new Rule.Example("foo", "bar"),
                        new Rule.Example("foo", null),
                        new Rule.Example(null, "bar")
                ), rule.examples);
        assertEquals(List.of("some", "message"), rule.justifications);
    }

    @Test
    public void testLoadRule3() {
        var rule = Rule.load(
                mapOf(
                        kv("id", "foo.bar.baz"),
                        kv("pattern", List.of("100", "_")),
                        kv("message", "message1"),
                        kv("tags", List.of("tag1", "tag2")),
                        kv("examples", mapOf(kv("before", "foo"), kv("after", "bar"))),
                        kv("justification", List.of("some", "message"))
                )
        );

        assertEquals("foo.bar.baz", rule.id);
        assertEquals(List.of("message1"), rule.messages);
        assertTrue(rule.patterns.get(0) instanceof AST.IntLiteral);
        assertTrue(rule.patterns.get(1) instanceof AST.Wildcard);
        assertEquals(Set.of("tag1", "tag2"), rule.tags);
        assertEquals(List.of(new Rule.Example("foo", "bar")), rule.examples);
        assertEquals(List.of("some", "message"), rule.justifications);
    }
}
