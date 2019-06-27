package com.github.sider.javasee;

import org.yaml.snakeyaml.Yaml;

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
            var rules = yaml.stream().map((hash) -> Rule.load(hash)).collect(Collectors.toList());
            requiredRules.addAll(rules);
        }
    }

    public static String getCommandLineName() {
        return System.getProperty("javasee.name", "java -jar JavaSee-all.jar");
    }

    public enum ExitStatus {
        OK(0),
        FAILURE(2),
        ERROR(1);

        private final int id;

        private ExitStatus(final int id) {
            this.id = id;
        }

        public int getInt() {
            return this.id;
        }
    }
}
