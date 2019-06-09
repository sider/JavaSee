package com.github.sider.java_see;

import com.github.sider.java_see.libs.Extentions;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Config {
    public final Set<Rule> rules;
    public final List<Check> checks;
    public final File rootDirectory;
    public final Map<List<Check>, Set<Rule>> rulesCache;

    public Config(Set<Rule> rules, List<Check> checks, File rootDirectory) {
        this.rules = rules;
        this.checks = checks;
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
        var matchingChecks = checks.stream().filter((check) -> check.matches(path)).collect(Collectors.toList());
        if(rulesCache.containsKey(matchingChecks)) {
            return rulesCache.get(matchingChecks);
        } else {
            Set<Rule> finalRules = allRules();
            for(Check check:matchingChecks) {
                for(Check.Query query:check.rules) {
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
            var rules = Extentions.single(yaml.get("rules")).stream().map(map -> Rule.load((Map<String, Object>)map)).collect(Collectors.toSet());
            var checks = Extentions.single(yaml.get("check")).stream().map(map -> Check.load((Map<String, Object>)map)).collect(Collectors.toList());
            return new Config(rules, checks, rootDirectory);
        }
    }
}
