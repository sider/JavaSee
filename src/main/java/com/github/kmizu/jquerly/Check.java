package com.github.kmizu.jquerly;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Check {
    @AllArgsConstructor
    @Getter
    @ToString
    public static class Query {
        public final String opr;
        public final Set<String> tags;
        public final String identifier;

        public Set<Rule> apply(Set<Rule> current, Set<Rule> all) {
            switch(opr) {
                case "append":
                    Set<Rule> newSet = new HashSet<>(current);
                    newSet.addAll(all.stream().filter(this::matches).collect(Collectors.toSet()));
                    return newSet;
                case "except":
                    return current.stream().filter((rule) -> !matches(rule)).collect(Collectors.toSet());
                case "only":
                    return all.stream().filter((rule) -> matches(rule)).collect(Collectors.toSet());
                default:
                    throw new RuntimeException("cannot reach here");
            }
        }

        public boolean matches(Rule rule) {
            return rule.matches(identifier, tags);
        }
    }

    public final List<String> patterns;
    public final List<Query> rules;

    public final boolean hasTrailingSlash;
    public final boolean hasMiddleSlash;

    public Check(String pattern, List<Query> rules) {
        this.rules = rules;
        this.hasTrailingSlash = pattern.endsWith("/");
        this.hasMiddleSlash = Pattern.compile(".*\\..*").matcher(pattern).matches();
        this.patterns = new ArrayList<>();

        pattern = pattern.replaceAll("\\A/", "");

        if(hasTrailingSlash && hasMiddleSlash) {
            patterns.add(pattern + File.separator + "**");
        } else if(hasTrailingSlash) {
            patterns.add(pattern + File.separator + "**");
            patterns.add("**" + File.separator + pattern + File.separator + "**");
        } else {
            patterns.add(pattern);
            patterns.add("**" + File.separator + pattern);
            patterns.add(pattern + File.separator + "**");
            patterns.add("**" + File.separator + pattern + File.separator + "**");
        }
    }

    public boolean hasTrainingSlash() {
        return hasTrailingSlash;
    }

    public boolean hasMiddleSlash() {
        return hasMiddleSlash;
    }

    private static List<?> single(Object object) {
        if(object instanceof List<?>) {
            return (List<?>)object;
        }else {
            return List.of(object);
        }
    }

    public static Check load(Map<String, Object> map) {
        String pattern = (String)map.get("path");
        List<Query> rules = single(map.get("rules")).stream().map((object) -> {
            if(object instanceof String) {
                return parseRuleQuery("append", (String)object);
            } else {
                Map<String, Object> rule = (Map<String, Object>)object;
                if(rule.containsKey("append")) {
                    return parseRuleQuery("append", rule.get("append"));
                }else if(rule.containsKey("except")) {
                    return parseRuleQuery("except", rule.get("except"));
                } else if (rule.containsKey("only")) {
                    return parseRuleQuery("only", rule.get("only"));
                } else {
                    return parseRuleQuery("append", rule);
                }
            }
        }).collect(Collectors.toList());

        return new Check(pattern, rules);
    }

    public static Query parseRuleQuery(String opr, Object query) {
        if(query instanceof String) {
            return new Query(opr, new HashSet<>(), (String)query);
        } else {
            Map<String, Object> queryMap = (Map<String, Object>)query;

            Set<String> tags;
            String identifier;
            if(queryMap.containsKey("tags")) {
                var ts = queryMap.get("tags");
                if(ts instanceof String) {
                    tags = new HashSet<String>(Arrays.asList(((String)ts).split(" ")));
                } else {
                    tags = new HashSet<String>((List<String>)ts);
                }
            } else {
                tags = new HashSet<>();
            }
            identifier = (String)queryMap.get("id");

            return new Query(opr, tags, identifier);
        }
    }


    public boolean matches(File path) {
        return patterns.stream().anyMatch((pattern) -> new File(pattern).equals(path));
    }
}
