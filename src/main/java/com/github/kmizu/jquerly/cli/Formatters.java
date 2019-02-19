package com.github.kmizu.jquerly.cli;

import com.github.kmizu.jquerly.NodePair;
import com.github.kmizu.jquerly.Rule;
import com.github.kmizu.jquerly.Script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Formatters {
    public static abstract class AbstractFormatter {
        /**
         * Called when analyzer started
         */
        public abstract void onStart();

        /**
         * Called when config is successfully loaded
         * @param config
         */
        public abstract void onConfigLoaded(Object config);
        /**
         * Called when failed to load config
         * Exit(status == 0) after the call
         */
        public abstract void onConfigError(Object path, Object error);

        /**
         * Called when script is successfully loaded
         * @param script
         */
        public abstract void onScriptLoaded(Object script);

        /**
         * Called when failed to load script
         * Continue after the call
         * @param path
         * @param error
         */
        public abstract void onScriptError(Object path, Object error);

        /**
         *
         * Called when issue is found
         * @param script
         * @param rule
         * @param pair
         */
        public abstract void onIssueFound(Script script, Rule rule, NodePair pair);

        /**
         * Called on other error
         * Abort(status != 0) after the call
         * @param error
         */
        public void onFatalError(String error) {
            System.err.println("Fatal error: " + error);
            //TODO print backtrace
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
        public void onConfigError(Object path, Object error) {
            System.err.println("Failed to load configuration: " + path);
            System.err.println(error);
            System.err.println("Backtrace:");
            //TODO print backtrace
        }

        /**
         * Called when script is successfully loaded
         *
         * @param script
         */
        @Override
        public void onScriptLoaded(Object script) {

        }

        /**
         * Called when failed to load script
         * Continue after the call
         *
         * @param path
         * @param error
         */
        @Override
        public void onScriptError(Object path, Object error) {
            System.err.println("Failed to load script: " + path);
            System.err.println(error);
        }

        /**
         * Called when issue is found
         *
         * @param script
         * @param rule
         * @param pair
         */
        @Override
        public void onIssueFound(Script script, Rule rule, NodePair pair) {
            var path = script.path;
            var src = pair.node.toString().split("\n")[0];
            var position = pair.node.getRange().get().begin;
            var line = position.line;
            var column = position.column;
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
         * Called on other error
         * Abort(status != 0) after the call
         *
         * @param error
         */
        @Override
        public void onFatalError(String error) {
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
        public void onConfigError(Object path, Object error) {
            configErrors.add(List.of(path, error));
        }

        /**
         * Called when script is successfully loaded
         *
         * @param script
         */
        @Override
        public void onScriptLoaded(Object script) {

        }

        /**
         * Called when failed to load script
         * Continue after the call
         *
         * @param path
         * @param error
         */
        @Override
        public void onScriptError(Object path, Object error) {
            scriptErrors.add(List.of(path, error));

        }

        /**
         * Called when issue is found
         *
         * @param script
         * @param rule
         * @param pair
         */
        @Override
        public void onIssueFound(Script script, Rule rule, NodePair pair) {
            issues.add(List.of(script, rule, pair));
        }


        public Map<String, Object> toJSON() {
            if (fatalError != null) {
                return Map.of("fatal_error", Map.of("message", fatalError.toString()));
            } else if (!configErrors.isEmpty()) {
                return Map.of(
                        "config_errors", configErrors.stream().map((arg) -> {
                            var path = (String) ((List<?>) arg).get(0);
                            var error = ((List<?>) arg).get(0);
                            return Map.of("path", path, "error", Map.of("message", error.toString()));
                        }).collect(Collectors.toList())
                );
            } else {
                return Map.of(
                        "issues", issues.stream().map((arg) -> {
                            List<?> args = (List<?>) arg;
                            Script script = (Script) args.get(0);
                            Rule rule = (Rule) args.get(1);
                            NodePair pair = (NodePair) args.get(2);
                            return Map.of(
                                    "script", script.path,
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
                            Object error = (Object) args.get(1);
                            return Map.of(
                                    "path", path,
                                    "error", Map.of(
                                            "message", error.toString()
                                    )
                            );
                        }).collect(Collectors.toList())
                );
            }
        }
    }
}
