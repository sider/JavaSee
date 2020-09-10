package com.github.sider.javasee;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.parser.JavaSeeParser;
import com.github.sider.javasee.parser.ParseException;

import java.io.File;
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

    private static final Set<String> KNOWN_KEYS = Set.of("id", "pattern", "message", "justification", "tests");

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

    public static Rule load(Map<String, Object> map, File configPath) throws Exceptions.MissingKeyException, Exceptions.UnknownKeysException, Exceptions.PatternSyntaxException {
        Set<String> unknownKeys = new HashSet<>();
        for(String key:map.keySet()) {
            if(!KNOWN_KEYS.contains(key)) {
                unknownKeys.add(key);
            }
        }
        if(!unknownKeys.isEmpty()) {
            throw new Exceptions.UnknownKeysException(configPath, unknownKeys, KNOWN_KEYS);
        }

        // Confirm that value of `id` is present
        var id = Optional.ofNullable((String)map.get("id"));
        if(!id.isPresent()){
            throw new Exceptions.MissingKeyException(configPath, "id is missing");
        }

        // Validate value of `pattern` is String or String[] or null
        validate: {
            Object value = map.get("pattern");
            if(value == null) break validate;
            if(value instanceof String) break validate;
            if(value instanceof List<?>) {
                List<?> values = (List<?>)value;
                if(values.stream().allMatch(v -> (v instanceof String))) break validate;
            }
            throw new Exceptions.InvalidTypeException(configPath, "pattern should be String or List<String>.  However, it's " + value);
        }
        List<?> srcs = valuesOf(map, "pattern");

        // Confirm that value of `pattern` is present
        if(srcs.isEmpty()) {
            throw new Exceptions.MissingKeyException(configPath, "pattern is missing");
        }

        int index = 0;
        var patterns = srcs.stream().map((src) -> {
            Optional<String> subject = Optional.empty();
            Optional<Map<String, String>> where = Optional.empty();
            if(src instanceof String) {
                subject = Optional.ofNullable((String)src);
                where = Optional.of(new HashMap<>());
            } else if(src instanceof Map<?, ?>) {
                subject = Optional.ofNullable((String)((Map<?, ?>)src).get("subject"));
                where = Optional.ofNullable((Map<String, String>)((Map<?, ?>) src).get("where"));
            } else {
                assert false;
            }
            try {
                if(subject.isPresent()) {
                    return new JavaSeeParser(new StringReader(subject.get())).WholeExpression();
                } else {
                    // Confirm that value of `subject` is present
                    throw new Exceptions.MissingKeyException(configPath, "subject");
                }
            } catch (ParseException e) {
                // Illegal pattern
                throw new Exceptions.PatternSyntaxException(
                    configPath,
                    "Pattern syntax error: rule=" + map.get("id") + ", index=" + index + ", pattern=" + subject + ", where=" + where, e
                );
            }

        }).collect(Collectors.toList());

        // Confirm that value of `message` is present
        var message = Optional.ofNullable((String)map.get("message")).orElseThrow(() -> new Exceptions.MissingKeyException(configPath, "message"));

        List<String> matchExamples = new ArrayList<>();
        List<String> unmatchExamples = new ArrayList<>();
        Optional.ofNullable((Map<String, Object>)map.get("tests")).ifPresent((tests) -> {
            matchExamples.addAll((List<String>)valuesOf(tests, "match"));
            unmatchExamples.addAll((List<String>)valuesOf(tests, "unmatch"));
        });

        List<String> justifications = (List<String>)valuesOf(map, "justification");

        return new Rule(
                id.get(),
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
