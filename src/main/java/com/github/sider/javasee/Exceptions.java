package com.github.sider.javasee;

import java.io.File;
import java.util.Collection;

public class Exceptions {
    public static class JavaSeeException extends RuntimeException {
        public JavaSeeException(String message, Exception e) {
            super(message, e);
        }
        public JavaSeeException(String message) { this(message, null); }
    }

    public static class ConfigFileException extends JavaSeeException {
        public final File configPath;
        public final JavaSee.ExitStatus status;
        public ConfigFileException(File configPath, String message, JavaSee.ExitStatus status, Exception e) {
            super(message, e);
            this.configPath = configPath;
            this.status = status;
        }
        public ConfigFileException(File configFile, String message, JavaSee.ExitStatus status) {
            this(configFile, message, status, null);
        }

    }

    public static class YamlValidationException extends JavaSeeException {
        public final File configPath;
        public YamlValidationException(File configPath, String message, Exception e) {
            super(message, e);
            this.configPath = configPath;
        }
        public YamlValidationException(File configPath, String message) {
            this(configPath, message, null);
        }
    }

    public static class InvalidRuleException extends YamlValidationException {
        public InvalidRuleException(File configPath, String message, Exception e) {
            super(configPath, message, e);
        }
        public InvalidRuleException(File configPath, String message) {
            this(configPath, message, null);
        }
    }

    public static class InvalidTypeException extends JavaSeeException {
        public final File configPath;
        public InvalidTypeException(File configPath, String message) {
            super(message);
            this.configPath = configPath;
        }

    }

    public static class UnknownFormatException extends JavaSeeException {
        public UnknownFormatException(String value) {
            super("Unknown format: " + value);
        }
    }

    public static class UnknownKeysException extends YamlValidationException {
        public UnknownKeysException(File configPath, Collection<String> actualKeys, Collection<String> allowedKeys) {
            super(configPath, "Unknown keys: " + actualKeys + ", only " + allowedKeys + " are allowed");
        }

    }

    public static class MissingKeyException extends YamlValidationException {
        public MissingKeyException(File configPath, String keyName) {
            super(configPath, "Missing key: " + keyName);
        }
    }

    public static class PatternSyntaxException extends InvalidRuleException {
        public PatternSyntaxException(File configPath, String message, Exception e) {
            super(configPath, message, e);
        }
    }
}
