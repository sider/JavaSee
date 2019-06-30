package com.github.sider.javasee;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.parser.JavaSeeParser;
import com.github.sider.javasee.parser.ParseException;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class Rule {
    public final String id;
    public final String message;
    public final List<AST.Expression> patterns;
    public final List<?> sources;
    public final List<String> matchExamples;
    public final List<String> unmatchExamples;
    public final List<String> justifications;

    public Rule(
            String id, String message, List<AST.Expression> patterns, List<?> sources,
            List<String> matchExamples, List<String> unmatchExamples, List<String> justifications) {
        this.id = id;
        this.message = message;
        this.patterns = patterns;
        this.sources = sources;
        this.matchExamples = matchExamples;
        this.unmatchExamples = unmatchExamples;
        this.justifications = justifications;
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

        List<String> matchExamples = new ArrayList<>();
        List<String> unmatchExamples = new ArrayList<>();
        var tests = (Map<String, Object>)map.get("tests");
        if (tests != null) {
            matchExamples.addAll((List<String>)valuesOf(tests, "match"));
            unmatchExamples.addAll((List<String>)valuesOf(tests, "unmatch"));
        }

        List<String> justifications = (List<String>)valuesOf(map, "justification");

        return new Rule(
                id,
                message,
                patterns,
                srcs,
                matchExamples,
                unmatchExamples,
                justifications
        );
    }

    private static List<?> valuesOf(Map<String, Object> map, String key) {
        Optional<Object> valueOpt;
        valueOpt = Optional.ofNullable(map.get(key));

        return valueOpt.map((value) -> value instanceof List<?> ? (List<Object>)value : List.of(value))
                       .orElse(new ArrayList<>());
    }
}
