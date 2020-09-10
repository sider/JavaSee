package com.github.sider.javasee;

import com.github.sider.javasee.lib.Extentions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Config {
    private static final Set<String> TOP_LEVEL_KNOWN_KEYS = Set.of("import", "rules");
    public final List<Rule> rules;
    public final File rootDirectory;

    public Config(List<Rule> rules, File rootDirectory) {
        this.rules = rules;
        this.rootDirectory = rootDirectory;
    }

    public void readFrom(File configPath, boolean imported) throws Exceptions.ConfigFileException, FileNotFoundException  {
        if(!configPath.isFile() || !configPath.exists()) {
            String message;
            if(imported) {
                message = configPath + " being imported is not found ...";
            } else {
                message =
                    "There is nothing to test at " + configPath + " ...\n" +
                    "Make a configuration and run test again!";
            }
            throw new Exceptions.ConfigFileException(configPath, message, JavaSee.ExitStatus.CONFIG_FILE_NOT_FOUND);
        }
        Map<String, Object> yaml = new Yaml().load(new FileInputStream(configPath));
        init(yaml, configPath);
    }

    private void init(Map<String, Object> yaml, File configPath) throws FileNotFoundException {
        if(yaml == null) {
            throw new Exceptions.ConfigFileException(configPath, "YAML file + \"" + configPath + "\"" + " has unknown error", JavaSee.ExitStatus.CONFIG_FILE_UNKNOWN_ERROR);
        }

        Set<String> unknownKeys = new HashSet<>();
        for(String key:yaml.keySet()) {
            if(!TOP_LEVEL_KNOWN_KEYS.contains(key)) {
                unknownKeys.add(key);
            }
        }

        if(!unknownKeys.isEmpty()){
            throw new Exceptions.UnknownKeysException(configPath, unknownKeys, TOP_LEVEL_KNOWN_KEYS);
        }

        Object imports = yaml.get("import");
        if(imports != null) {
            for(Object entry:Extentions.single(imports)) {
                File configFile = new File((String)entry);
                readFrom(configFile, true);
            }
        }
        Object object = yaml.get("rules");
        if(object == null) {
            throw new Exceptions.MissingKeyException(configPath, "rules");
        }
        var additionalRules = Extentions.single(object).stream().map(map -> Rule.load((Map<String, Object>)map, configPath)).collect(Collectors.toList());
        rules.addAll(additionalRules);
    }

    public static Config empty(File rootDirectory) {
        var config = new Config(new ArrayList<>(), rootDirectory);
        return config;
    }

    public static Config load(Map<String, Object> yaml, File configPath, File rootDirectory) throws Exceptions.ConfigFileException, FileNotFoundException {
        var config = Config.empty(rootDirectory);
        config.init(yaml, configPath);
        return config;
    }

    public static Config load(File configPath, File rootDirectory) throws Exceptions.ConfigFileException, FileNotFoundException {
        var config = Config.empty(rootDirectory);
        config.readFrom(configPath, false);
        return config;
    }
}
