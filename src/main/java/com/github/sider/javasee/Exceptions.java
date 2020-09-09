package com.github.sider.javasee;

import java.util.Collection;

public class Exceptions {
    public static class JavaSeeException extends RuntimeException {
        public JavaSeeException(String message, Exception e) {
            super(message, e);
        }
        public JavaSeeException(String message) { this(message, null); }
    }

    public static class YamlValidationException extends JavaSeeException {
        public YamlValidationException(String message, Exception e) {
            super(message, e);
        }
        public YamlValidationException(String message) { this(message, null); }
    }

    public static class InvalidRuleException extends YamlValidationException {
        public InvalidRuleException(String message, Exception e) {
            super(message, e);
        }
        public InvalidRuleException(String message) {
            this(message, null);
        }
    }

    public static class InvalidTypeException extends JavaSeeException {
        public InvalidTypeException(String message) {
            super(message);
        }

    }

    public static class UnknownFormatException extends JavaSeeException {
        public UnknownFormatException(String value) {
            super("Unknown format: " + value);
        }
    }

    public static class UnknownKeysException extends YamlValidationException {
        public UnknownKeysException(Collection<String> actualKeys, Collection<String> allowedKeys) {
            super("Unknown keys: " + actualKeys + ", only " + allowedKeys + " are allowed");
        }

    }

    public static class MissingKeyException extends YamlValidationException {
        public MissingKeyException(String keyName) {
            super("Missing key: " + keyName);
        }
    }

    public static class PatternSyntaxException extends InvalidRuleException {
        public PatternSyntaxException(String message, Exception e) {
            super(message, e);
        }
    }
}
