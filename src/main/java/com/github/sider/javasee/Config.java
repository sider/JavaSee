package com.github.sider.javasee;

import com.github.sider.javasee.lib.Extentions;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Config {
    public final List<Rule> rules;
    public final List<Matcher> matchers;
    public final File rootDirectory;
    public final Map<List<Matcher>, Set<Rule>> rulesCache;

    public Config(List<Rule> rules, List<Matcher> matchers, File rootDirectory) {
        this.rules = rules;
        this.matchers = matchers;
        this.rootDirectory = rootDirectory;
        this.rulesCache = new HashMap<>();
    }

    public static Config load(Map<String, Object> map, File configPath, File rootDirectory) {
        return new Factory(map, configPath, rootDirectory).config();
    }

    private Set<Rule> allRules() {
        Set<Rule> newRules = new HashSet<>();
        newRules.addAll(rules);
        return newRules;
    }

    public Set<Rule> rulesForPath(File path) {
        var matchingChecks = matchers.stream().filter((matcher) -> matcher.matches(path)).collect(Collectors.toList());
        if(rulesCache.containsKey(matchingChecks)) {
            return rulesCache.get(matchingChecks);
        } else {
            Set<Rule> finalRules = allRules();
            for(Matcher check:matchingChecks) {
                for(Matcher.Query query:check.rules) {
                    finalRules = query.apply(finalRules, allRules());
                }
            }
            rulesCache.put(matchingChecks, finalRules);
            return finalRules;
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Factory {
        public final Map<String, Object> yaml;
        public final File configPath;
        public final File rootDirectory;

        public Config config() {
            var rules = Extentions.single(yaml.get("rules")).stream().map(map -> Rule.load((Map<String, Object>)map)).collect(Collectors.toList());
            var checks = Extentions.single(yaml.get("check")).stream().map(map -> Matcher.load((Map<String, Object>)map)).collect(Collectors.toList());
            return new Config(rules, checks, rootDirectory);
        }
    }
}
