package com.github.sider.javasee;

import com.github.sider.javasee.lib.Extentions;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Config {
    public final List<Rule> rules;
    public final File rootDirectory;

    public Config(List<Rule> rules, File rootDirectory) {
        this.rules = rules;
        this.rootDirectory = rootDirectory;
    }

    public static Config load(Map<String, Object> map, File configPath, File rootDirectory) {
        return new Factory(map, configPath, rootDirectory).config();
    }

    @AllArgsConstructor
    @Getter
    public static class Factory {
        public final Map<String, Object> yaml;
        public final File configPath;
        public final File rootDirectory;

        public Config config() {
            Object object = yaml.get("rules");
            if(object == null) {
                throw new Exceptions.MissingKeyException("rules");
            }
            var rules = Extentions.single(object).stream().map(map -> Rule.load((Map<String, Object>)map)).collect(Collectors.toList());
            return new Config(rules, rootDirectory);
        }
    }
}
