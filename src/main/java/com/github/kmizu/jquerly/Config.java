package com.github.kmizu.jquerly;

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
    /*
      class Config
    attr_reader :rules
    attr_reader :preprocessors
    attr_reader :root_dir
    attr_reader :checks
    attr_reader :rules_cache

    def initialize(rules:, preprocessors:, root_dir:, checks:)
      @rules = rules
      @root_dir = root_dir
      @preprocessors = preprocessors
      @checks = checks
      @rules_cache = {}
    end

    def all_rules
      @all_rules ||= Set.new(rules)
    end

    def relative_path_from_root(path)
      path.absolute? ? path.relative_path_from(root_dir) : path.cleanpath
    end

    def rules_for_path(path)
      relative_path = relative_path_from_root(path)
      matching_checks = checks.select {|check| check.match?(path: relative_path) }

      if rules_cache.key?(matching_checks)
        rules_cache[matching_checks]
      else
        matching_checks.flat_map(&:rules).inject(all_rules) do |rules, query|
          query.apply(current: rules, all: all_rules)
        end.tap do |rules|
          rules_cache[matching_checks] = rules
        end
      end
    end

     */

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

    private static List<?> single(Object object) {
        if(object instanceof List<?>) {
            return (List<?>)object;
        } else {
            if(object == null) {
                return List.of();
            } else {
                return List.of(object);
            }
        }
    }
    @AllArgsConstructor
    @Getter
    public static class Factory {
        public final Map<String, Object> yaml;
        public final File configPath;
        public final File rootDirectory;

        public Config config() {
            var rules = single(yaml.get("rules")).stream().map(map -> Rule.load((Map<String, Object>)map)).collect(Collectors.toSet());
            var checks = single(yaml.get("check")).stream().map(map -> Check.load((Map<String, Object>)map)).collect(Collectors.toList());

            return new Config(rules, checks, rootDirectory);
        }
    }
}
