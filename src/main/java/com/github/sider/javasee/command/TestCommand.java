package com.github.sider.javasee.command;

import com.github.sider.javasee.*;
import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.lib.ConsoleColors;
import com.github.sider.javasee.lib.Libs;
import com.github.sider.javasee.lib.Ref;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TestCommand extends CLICommand {
    private Config config;

    @Getter
    public final PrintStream stdout;

    @Getter
    public final PrintStream stderr;

    private boolean success;

    public TestCommand(Main.Options options, PrintStream stdout, PrintStream stderr) {
        super(options);
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public void fail() {
        success = false;
    }

    public boolean isFailed() {
        return !success;
    }

    @Override
    public boolean start() {
        options.config = "javasee.yml";
        try {
            if(!options.configPath().isFile()) {
                System.out.println("There is nothing to test at " + options.configPath() + " ...");
                System.out.println("Make a configuration and run test again!");
                return false;
            }
            File rootPath;
            if(options.root != null) {
                rootPath = new File(options.root);
            } else {
                rootPath = options.configPath().getParentFile();
            }
            Map<String, Object> yaml;
            try {
                yaml = new Yaml().load(new FileInputStream(options.configPath()));
                config = Config.load(yaml, options.configPath(), rootPath);
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        validateRuleUniqueness(config.rules);
        validateRulePatterns(config.rules);

        return isFailed() ? false : true;
    }

    private void validateRuleUniqueness(List<Rule> rules) {
        var ids = new HashSet<String>();

        stdout.println("Checking rule id uniqueness...");

        var duplications = 0;

        for(Rule rule:rules) {
            if(ids.contains(rule.id)) {
                stdout.println(ConsoleColors.red("  Rule id " + rule.id + "duplicated!"));
                duplications += 1;
            } else {
                ids.add(rule.id);
            }
        }

        if(duplications > 0) fail();
    }

    private void validateRulePatterns(List<Rule> rules) {
        stdout.println("Checkintg rule patterns...");

        var tests = 0;
        var falsePositives = 0;
        var falseNegatives = 0;
        var errors = 0;

        for(var rule:rules) {
            {
                int exampleIndex = 1;
                for (var exampleString : rule.beforeExamples) {
                    tests++;

                    try {
                        if (!rule.patterns.stream().anyMatch((pattern) -> testPattern(pattern, exampleString, true))) {
                            stdout.println(ConsoleColors.red("  " + rule.id) + ":\t" + Libs.ordinalize(exampleIndex) + " *before* example didn't match with any pattern");
                            falseNegatives += 1;
                        }
                    } catch (Exception e) {
                        errors += 1;
                        stdout.println("  " + ConsoleColors.red(rule.id) + ":\tParsing failed for " + Libs.ordinalize(exampleIndex) + " *before* example");
                    }
                    exampleIndex++;
                }
            }

            {
                int exampleIndex = 1;
                for(var exampleString:rule.afterExamples) { tests += 1;
                    try {
                        if (!rule.patterns.stream().allMatch((pattern) -> testPattern(pattern, exampleString, false))) {
                            stdout.println(ConsoleColors.red("  " + rule.id) + ":\t" + Libs.ordinalize(exampleIndex) + " *after* example matched with some of patterns");
                            falsePositives += 1;
                        }
                    } catch (Exception e) {
                        errors += 1;
                        stdout.println("  " + ConsoleColors.red(rule.id) + ":\tParsing failed for " + Libs.ordinalize(exampleIndex) + " *after* example");
                    }
                    exampleIndex++;
                }
            }
        }

        stdout.println("Tested " + rules.size() + " rules with " + tests + " tests");
        if(falsePositives > 0 || falseNegatives >0 || errors > 0) {
            stdout.println("  " + falsePositives + " examples found which should not match, but matched");
            stdout.println("  " + falseNegatives + " examples found which should match, but didn't");
            stdout.println("  " + errors + " examples have erros");
            fail();
        } else {
            stdout.println(ConsoleColors.green("  All tests green!"));
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
