package com.github.sider.javasee;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.parser.JavaSeeParser;
import com.github.sider.javasee.parser.ParseException;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class Rule {
    public static class Example {
        public final String before;
        public final String after;

        public Example(String before, String after) {
            this.before = before;
            this.after = after;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Example example = (Example) o;
            return Objects.equals(before, example.before) &&
                    Objects.equals(after, example.after);
        }

        @Override
        public int hashCode() {
            return Objects.hash(before, after);
        }
    }

    public final String id;
    public final String message;
    public final List<AST.Expression> patterns;
    public final List<?> sources;
    public final Set<String> tags;
    public final List<String> beforeExamples;
    public final List<String> afterExamples;
    public final List<String> justifications;
    public final List<Example> examples;

    public Rule(
            String id, String message, List<AST.Expression> patterns, List<?> sources, Set<String> tags,
            List<String> beforeExamples, List<String> afterExamples, List<String> justifications, List<Example> examples) {
        this.id = id;
        this.message = message;
        this.patterns = patterns;
        this.sources = sources;
        this.tags = tags;
        this.beforeExamples = beforeExamples;
        this.afterExamples = afterExamples;
        this.justifications = justifications;
        this.examples = examples;
    }

    public boolean matches(String identifier, Set<String> tags) {
        if(identifier != null) {
            if((!id.equals(identifier)) && (!id.startsWith(identifier + "."))) {
                return false;
            }
        }

        if(tags != null) {
            if(!this.tags.containsAll(tags)) {
                return false;
            }
        }

        return true;
    }

    public static class InvalidRuleMapException extends RuntimeException {
        public InvalidRuleMapException(String message) {
            super(message);
        }
    }

    public static class PatternSyntaxException extends RuntimeException {
        public PatternSyntaxException(String message, Exception e) {
            super(message, e);
        }

    }

    public static Rule load(Map<String, Object> map) {
        String id = (String)map.get("id");
        if(id == null) {
            throw new InvalidRuleMapException("id is missing");
        }

        List<?> srcs = valuesOf(map, "pattern");

        if(srcs.isEmpty()) {
            throw new InvalidRuleMapException("pattern is missing");
        }

        int index = 0;
        var patterns = srcs.stream().map((src) -> {
            String subject = null;
            Map<String, String> where = null;
            if(src instanceof String) {
                subject = (String)src;
                where = new HashMap<>();
            } else if(src instanceof Map<?, ?>) {
                subject = (String)((Map<?, ?>)src).get("subject");
                where = (Map<String, String>)((Map<?, ?>) src).get("where");
            } else {
                assert false;
            }
            try {
                return new JavaSeeParser(new StringReader(subject)).WholeExpression();
            } catch (ParseException e) {
                throw new PatternSyntaxException(
                        "Pattern syntax error: rule=" + map.get("id") + ", index=" + index + ", pattern=" + subject + ", where=" + where, e
                );
            }

        }).collect(Collectors.toList());

        var message = (String)map.getOrDefault("message", null);

        if(message == null) {
            throw new InvalidRuleMapException("message is missing");
        }

        var tags = new HashSet<String>();
        tags.addAll((List<String>)valuesOf(map, "tags"));

        var examples = valuesOf(map, "examples").stream().map((ex) -> {
            Map<String, Object> example = (Map<String, Object>)ex;
            if((!example.containsKey("before")) && (!example.containsKey("after"))) {
                throw new InvalidRuleMapException("Example should have at least before or after: " + example);
            }
            return new Example((String)example.get("before"), (String)example.get("after"));
        }).collect(Collectors.toList());

        List<String> beforeExamples = (List<String>)valuesOf(map, "before");
        List<String> afterExamples = (List<String>)valuesOf(map, "after");
        List<String> justifications = (List<String>)valuesOf(map, "justification");

        return new Rule(
                id,
                message,
                patterns,
                srcs,
                tags,
                beforeExamples,
                afterExamples,
                justifications,
                examples
        );
    }

    private static List<?> valuesOf(Map<String, Object> map, String key) {
        Object value = (Object)map.get(key);
        if(value == null) {
            return new ArrayList<Object>();
        } else if(value instanceof List<?>) {
            return (List<Object>)value;
        } else {
            List<Object> values = new ArrayList<>();
            values.add(value);
            return values;
        }
    }
}
