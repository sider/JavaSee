package com.github.sider.javasee.command;

import com.github.sider.javasee.*;
import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.lib.ConsoleColors;
import com.github.sider.javasee.lib.Libs;
import com.github.sider.javasee.lib.Ref;
import org.kohsuke.args4j.Option;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TestCommand implements CLICommand {
    @Option(name="-config", aliases = "--config", metaVar = "<config>", usage = "config YAML file", help = true)
    public String optionConfig = "javasee.yml";

    private boolean success;

    public void fail() {
        success = false;
    }

    public boolean isFailed() {
        return !success;
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public boolean start(PrintStream out, PrintStream err) {
        Config config;
        try {
            var configPath = new File(optionConfig);
            if(!configPath.isFile()) {
                out.println("There is nothing to test at " + configPath + " ...");
                err.println("Make a configuration and run test again!");
                return false;
            }
            File rootPath;
            if(configPath != null) {
                rootPath = configPath.getParentFile();
            } else {
                rootPath = new File("");
            }
            Map<String, Object> yaml;
            try {
                yaml = new Yaml().load(new FileInputStream(configPath));
                config = Config.load(yaml, configPath, new File("."));
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        validateRuleUniqueness(out, config.rules);
        validateRulePatterns(out, config.rules);

        return isFailed() ? false : true;
    }

    private void validateRuleUniqueness(PrintStream out, List<Rule> rules) {
        var ids = new HashSet<String>();

        out.println("Checking rule id uniqueness...");

        var duplications = 0;

        for(Rule rule:rules) {
            if(ids.contains(rule.id)) {
                out.println(ConsoleColors.red("  Rule id " + rule.id + "duplicated!"));
                duplications += 1;
            } else {
                ids.add(rule.id);
            }
        }

        if(duplications > 0) fail();
    }

    private void validateRulePatterns(PrintStream out, List<Rule> rules) {
        out.println("Checkintg rule patterns...");

        var tests = 0;
        var falsePositives = 0;
        var falseNegatives = 0;
        var errors = 0;

        for(var rule:rules) {
            {
                int exampleIndex = 1;
                for (var exampleString : rule.matchExamples) {
                    tests++;

                    try {
                        if (!rule.patterns.stream().anyMatch((pattern) -> testPattern(pattern, exampleString, true))) {
                            out.println(ConsoleColors.red("  " + rule.id) + ":\t" + Libs.ordinalize(exampleIndex) + " *before* example didn't match with any pattern");
                            falseNegatives += 1;
                        }
                    } catch (Exception e) {
                        errors += 1;
                        out.println("  " + ConsoleColors.red(rule.id) + ":\tParsing failed for " + Libs.ordinalize(exampleIndex) + " *before* example");
                    }
                    exampleIndex++;
                }
            }

            {
                int exampleIndex = 1;
                for(var exampleString:rule.unmatchExamples) { tests += 1;
                    try {
                        if (!rule.patterns.stream().allMatch((pattern) -> testPattern(pattern, exampleString, false))) {
                            out.println(ConsoleColors.red("  " + rule.id) + ":\t" + Libs.ordinalize(exampleIndex) + " *after* example matched with some of patterns");
                            falsePositives += 1;
                        }
                    } catch (Exception e) {
                        errors += 1;
                        out.println("  " + ConsoleColors.red(rule.id) + ":\tParsing failed for " + Libs.ordinalize(exampleIndex) + " *after* example");
                    }
                    exampleIndex++;
                }
            }
        }

        out.println("Tested " + rules.size() + " rules with " + tests + " tests");
        if(falsePositives > 0 || falseNegatives >0 || errors > 0) {
            out.println("  " + falsePositives + " examples found which should not match, but matched");
            out.println("  " + falseNegatives + " examples found which should match, but didn't");
            out.println("  " + errors + " examples have erros");
            fail();
        } else {
            out.println(ConsoleColors.green("  All tests green!"));
        }
    }

    private boolean testPattern(AST.Expression pattern, String exampleString, boolean expected) {
        var analyzer = new Analyzer(null, null, null);
        var found = Ref.of(false);

        var node = new JavaParser().parseExpression(exampleString);
        new NodePair(node, null).eachSubPair((pair) -> {
            if(analyzer.testPair(pair, pattern)) {
                found.set(true);
            }
        });

        return found.get() == expected;
    }
}
