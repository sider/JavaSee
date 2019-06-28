package com.github.sider.javasee.command;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.Node;
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

class JavaParserError extends RuntimeException {
    public String source;

    JavaParserError(String source, Throwable cause) {
        super();
        this.initCause(cause);
        this.source = source;
    }
}

public class TestCommand implements CLICommand {
    @Option(name="-config", aliases = "--config", metaVar = "<config>", usage = "config YAML file", help = true)
    public String optionConfig = "javasee.yml";

    private boolean success = true;

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
    public JavaSee.ExitStatus start(PrintStream out, PrintStream err) {
        Config config;
        try {
            var configPath = new File(optionConfig);
            if(!configPath.isFile()) {
                out.println("There is nothing to test at " + configPath + " ...");
                err.println("Make a configuration and run test again!");
                return JavaSee.ExitStatus.ERROR;
            }

            Map<String, Object> yaml = new Yaml().load(new FileInputStream(configPath));
            config = Config.load(yaml, configPath, new File("."));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        validateRuleUniqueness(out, config.rules);
        validateRulePatterns(out, err, config.rules);

        return isFailed() ? JavaSee.ExitStatus.FAILURE : JavaSee.ExitStatus.OK;
    }

    public void validateRuleUniqueness(PrintStream out, List<Rule> rules) {
        var ids = new HashSet<String>();

        out.println("Checking rule id uniqueness...");

        var duplications = 0;

        for(Rule rule:rules) {
            if(ids.contains(rule.id)) {
                out.println(ConsoleColors.red(String.format("  Rule id `%s` duplicated!", rule.id)));
                duplications += 1;
            } else {
                ids.add(rule.id);
            }
        }

        if(duplications > 0) fail();
    }

    public void validateRulePatterns(PrintStream out, PrintStream err, List<Rule> rules) {
        out.println("Checking rule patterns...");

        var tests = 0;
        var falsePositives = 0;
        var falseNegatives = 0;
        var errors = 0;

        for(var rule:rules) {
            try {
                {
                    int exampleIndex = 1;
                    for (var exampleString : rule.matchExamples) {
                        if (!rule.patterns.stream().anyMatch((pattern) -> testPattern(pattern, exampleString, true))) {
                            out.println(ConsoleColors.red(String.format("  %s", rule.id)) + String.format(":\t%s match example didn't match with any pattern", Libs.ordinalize(exampleIndex)));
                            falseNegatives += 1;
                        }

                        tests++;
                        exampleIndex++;
                    }
                }

                {
                    int exampleIndex = 1;
                    for(var exampleString:rule.unmatchExamples) {
                        if (!rule.patterns.stream().allMatch((pattern) -> testPattern(pattern, exampleString, false))) {
                            out.println(ConsoleColors.red(String.format("  %s", rule.id)) + String.format(":\t%s unmatch example matched with some of patterns", Libs.ordinalize(exampleIndex)));
                            falsePositives += 1;
                        }

                        tests += 1;
                        exampleIndex++;
                    }
                }
            } catch (JavaParserError error) {
                err.println(String.format("Failed to parse an example in `%s`", rule.id));
                err.println("  Examples should be one of Java expression, statement, or compilation unit.");
                err.println(String.format("    %s", error.source.split("\n")[0]));
                errors++;
            }
        }

        out.println(String.format("Tested %d rules with %d tests", rules.size(), tests));
        if(falsePositives > 0 || falseNegatives >0 | errors > 0) {
            out.println("  " + falsePositives + " examples found which should not match, but matched");
            out.println("  " + falseNegatives + " examples found which should match, but didn't");
            out.println(String.format("  %d errors reported", errors));
            fail();
        } else {
            out.println(ConsoleColors.green("  All tests green!"));
        }
    }

    private boolean testPattern(AST.Expression pattern, String exampleString, boolean expected) throws JavaParserError {
        var analyzer = new Analyzer(null, null, null);
        var found = Ref.of(false);

        try {
            var node = parse(exampleString);
            new NodePair(node, null).eachSubPair((pair) -> {
                if(analyzer.testPair(pair, pattern)) {
                    found.set(true);
                }
            });

            return found.get() == expected;
        } catch (ParseProblemException e) {
            throw new JavaParserError(exampleString, e);
        }
    }

    private Node parse(String example) {
        try {
            return new JavaParser().parseExpression(example);
        } catch (ParseProblemException e1) {
            try {
                return new JavaParser().parseStatement(example);
            } catch (ParseProblemException e2) {
                return new JavaParser().parse(example);
            }
        }
    }
}
