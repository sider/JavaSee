package com.github.sider.javasee;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaSee {
    private static List<Rule> requiredRules = new ArrayList<>();
    public static List<Rule> getRequiredRules() {
        return requiredRules;
    }

    public static void loadRules(String... fileNames) throws IOException  {
        for(String fileName:fileNames) {
            var path = Paths.get(fileName);
            var content = Files.readString(path);
            List<Map<String, Object>> yaml = new Yaml().load(content);
            var rules = yaml.stream().map((hash) -> Rule.load(hash, new File(fileName))).collect(Collectors.toList());
            requiredRules.addAll(rules);
        }
    }

    public static String getCommandLineName() {
        return System.getProperty("javasee.name", "java -jar JavaSee-all.jar");
    }

    public enum ExitStatus {
        OK(0),
        ERROR(1),
        FAILURE(2),
        CONFIG_FILE_NOT_FOUND(3),
        CONFIG_FILE_SYNTAX_ERROR(4),
        CONFIG_FILE_SCHEMA_ERROR(5),
        CONFIG_FILE_UNKNOWN_ERROR(6);

        private final int id;

        ExitStatus(final int id) {
            this.id = id;
        }

        public int getInt() {
            return this.id;
        }
    }
}
