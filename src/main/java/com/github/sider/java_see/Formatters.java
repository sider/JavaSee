package com.github.sider.java_see;

import com.github.sider.java_see.lib.ConsoleColors;
import com.github.sider.java_see.lib.Libs;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Formatters {
    public static abstract class AbstractFormatter implements StacktraceFormatting {
        /**
         * Called when analyzer started
         */
        public abstract void onStart();

        /**
         * Called when analyzer finished
         */
        public abstract void onFinish();

        /**
         * Called when config is successfully loaded
         * @param config
         */
        public abstract void onConfigLoaded(Object config);
        /**
         * Called when failed to load config
         * Exit(status == 0) after the call
         */
        public abstract void onConfigError(String path, Exception error);

        /**
         * Called when javaFile is successfully loaded
         * @param javaFile
         */
        public abstract void onScriptLoaded(JavaFile javaFile);

        /**
         * Called when failed to load script
         * Continue after the call
         * @param path
         * @param error
         */
        public abstract void onScriptError(String path, Exception error);

        /**
         *
         * Called when issue is found
         * @param javaFile
         * @param rule
         * @param pair
         */
        public abstract void onIssueFound(JavaFile javaFile, Rule rule, NodePair pair);

        /**
         * Called on other error
         * Abort(status != 0) after the call
         * @param error
         */
        public void onFatalError(Exception error) {
            System.err.println("Fatal error: " + error);
            System.err.println("Backtrace:");
            System.err.println(formatStacktrace(error.getStackTrace(), 2));
        }
    }

    public static class TextFormatter extends AbstractFormatter {
        /**
         * Called when analyzer started
         */
        @Override
        public void onStart() {

        }

        /**
         * Called when analyzer finished
         */
        @Override
        public void onFinish() {

        }

        /**
         * Called when config is successfully loaded
         *
         * @param config
         */
        @Override
        public void onConfigLoaded(Object config) {

        }

        /**
         * Called when failed to load config
         * Exit(status == 0) after the call
         *
         * @param path
         * @param error
         */
        @Override
        public void onConfigError(String path, Exception error) {
            System.err.println("Failed to load configuration: " + path);
            System.err.println(error);
            System.err.println("Backtrace:");
            //TODO print backtrace
        }

        /**
         * Called when javaFile is successfully loaded
         *
         * @param javaFile
         */
        @Override
        public void onScriptLoaded(JavaFile javaFile) {

        }

        /**
         * Called when failed to load script
         * Continue after the call
         *
         * @param path
         * @param error
         */
        @Override
        public void onScriptError(String path, Exception error) {
            System.err.println("Failed to load script: " + path);
            System.err.println(error);
        }

        /**
         * Note that line is 1-origin
         */
        private String getLine(File path, int line) {
            var lines = Libs.wrapException(() -> Files.readAllLines(path.toPath()));
            return lines.get(line - 1);
        }

        /**
         * Called when issue is found
         *
         * @param javaFile
         * @param rule
         * @param pair
         */
        @Override
        public void onIssueFound(JavaFile javaFile, Rule rule, NodePair pair) {
            var path = javaFile.path;
            var position = pair.node.getRange().get().begin;
            var line = position.line;
            var column = position.column;
            var src = ConsoleColors.red(getLine(path, line));
            var message = rule.messages.get(0).split("\n")[0];
            System.out.println(path + ":" + line + ":" + column + "\t" + src + "\t" + message + "(" + rule.id + ")");
        }
    }

    public static class JSONFormatter extends AbstractFormatter {
        public final List<Object> issues = new ArrayList<>();
        public final List<Object> scriptErrors = new ArrayList<>();
        public final List<Object> configErrors = new ArrayList<>();
        public Object fatalError;

        /**
         * Called when analyzer started
         */
        @Override
        public void onStart() {

        }

        /**
         * Called when analyzer finished
         */
        @Override
        public void onFinish() {
            System.out.print(asJSON(toJSON(), 0));
        }

        /**
         * Called on other error
         * Abort(status != 0) after the call
         *
         * @param error
         */
        @Override
        public void onFatalError(Exception error) {
            super.onFatalError(error);
            fatalError = error;
        }

        /**
         * Called when config is successfully loaded
         *
         * @param config
         */
        @Override
        public void onConfigLoaded(Object config) {

        }

        /**
         * Called when failed to load config
         * Exit(status == 0) after the call
         *
         * @param path
         * @param error
         */
        @Override
        public void onConfigError(String path, Exception error) {
            configErrors.add(List.of(path, error));
        }

        /**
         * Called when javaFile is successfully loaded
         *
         * @param javaFile
         */
        @Override
        public void onScriptLoaded(JavaFile javaFile) {
        }

        /**
         * Called when failed to load script
         * Continue after the call
         *
         * @param path
         * @param error
         */
        @Override
        public void onScriptError(String path, Exception error) {
            scriptErrors.add(List.of(path, error));

        }

        /**
         * Called when issue is found
         *
         * @param javaFile
         * @param rule
         * @param pair
         */
        @Override
        public void onIssueFound(JavaFile javaFile, Rule rule, NodePair pair) {
            issues.add(List.of(javaFile, rule, pair));
        }

        private static String indent(int indentLevel) {
            var builder = new StringBuilder();
            for(int i = 0; i < indentLevel; i++) {
                builder.append("  ");
            }
            return new String(builder);
        }

        public String asJSON(Object jvalue, int indentLevel) {
            var builder = new StringBuilder();
            if(jvalue instanceof Map<?, ?>) {
                var object = (Map<String, Object>) jvalue;
                builder.append("{\n");
                indentLevel++;
                var entries = object.entrySet().stream().collect(Collectors.toList());
                boolean first = true;
                for (var entry : entries) {
                    if (!first) {
                        builder.append(",\n");
                    }
                    builder.append(indent(indentLevel));
                    builder.append("\"");
                    builder.append(entry.getKey());
                    builder.append("\"");
                    builder.append(":");
                    var value = entry.getValue();
                    builder.append(asJSON(value, indentLevel));
                    first = false;
                }
                builder.append("}\n");
            } else if(jvalue instanceof List<?>) {
                var array = (List<Object>)jvalue;
                builder.append("[");
                indentLevel++;
                boolean first = true;
                for(var value: array) {
                    if (!first) {
                        builder.append(", ");
                    }
                    builder.append(asJSON(value, indentLevel));
                    first = false;
                }
                builder.append("]");
            } else if(jvalue instanceof String) {
                var string = (String)jvalue;
                builder.append("\"");
                for(char ch:string.toCharArray()) {
                    if(ch == '\n') {
                        builder.append("\\");
                        builder.append("n");
                    } else if(ch == '\r') {
                        builder.append("\\");
                        builder.append("r");
                    } else {
                        builder.append(ch);
                    }
                }
                builder.append("\"");
            } else if(jvalue instanceof Integer) {
                builder.append(((Integer)jvalue).intValue());
            } else if(jvalue instanceof Double) {
                builder.append(((Double)jvalue).doubleValue());
            } else if(jvalue instanceof Boolean) {
                builder.append(((Boolean)jvalue).booleanValue());
            } else {
                throw new RuntimeException("cannot reach here: " + jvalue);
            }
            return new String(builder);
        }


        public Map<String, Object> toJSON() {
            if (fatalError != null) {
                Exception e = (Exception)fatalError;
                return Map.of("fatal_error",
                        Map.of(
                                "message", e.getMessage(),
                                "backtrace", Arrays.asList(e.getStackTrace()).stream().map((x) -> x.toString()).collect(Collectors.toList())
                        )
                );
            } else if (!configErrors.isEmpty()) {
                return Map.of(
                        "config_errors", configErrors.stream().map((arg) -> {
                            var path = (String) ((List<?>) arg).get(0);
                            var error = (Exception)((List<?>) arg).get(0);
                            return Map.of("path", path, "error",
                                    Map.of(
                                            "message", error.getMessage(),
                                            "backtrace", Arrays.asList(error.getStackTrace()).stream().map((x) -> x.toString()).collect(Collectors.toList())
                                    )
                            );
                        }).collect(Collectors.toList())
                );
            } else {
                return Map.of(
                        "issues", issues.stream().map((arg) -> {
                            List<?> args = (List<?>) arg;
                            JavaFile javaFile = (JavaFile) args.get(0);
                            Rule rule = (Rule) args.get(1);
                            NodePair pair = (NodePair) args.get(2);
                            return Map.of(
                                    "javaFile", javaFile.path.getPath(),
                                    "rule", Map.of(
                                            "id", rule.id,
                                            "messages", rule.messages,
                                            "justifications", rule.justifications,
                                            "examples", rule.examples.stream().map(
                                                    (example) -> Map.of("before", example.before, "after", example.after)
                                            ).collect(Collectors.toList())
                                    ),
                                    "location", Map.of(
                                            "start", List.of(pair.node.getRange().get().begin.line, pair.node.getRange().get().begin.column),
                                            "end", List.of(pair.node.getRange().get().end.line, pair.node.getRange().get().end.column)
                                    )
                            );
                        }).collect(Collectors.toList()),
                        "errors", scriptErrors.stream().map((arg) -> {
                            List<?> args = (List<?>) arg;
                            String path = (String) args.get(0);
                            Exception error = (Exception) args.get(1);
                            return Map.of(
                                    "path", path,
                                    "error", Map.of(
                                            "message", error.getMessage(),
                                            "backtrace", Arrays.asList(error.getStackTrace()).stream().map((x) -> x.toString()).collect(Collectors.toList())
                                    )
                            );
                        }).collect(Collectors.toList())
                );
            }
        }
    }
}
