package com.github.sider.java_see;

import com.github.sider.java_see.ast.AST;
import com.github.sider.java_see.libs.ConsoleColors;
import com.github.sider.java_see.libs.Libs;
import com.github.sider.java_see.libs.Ref;
import lombok.Getter;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class Test {
    @Getter
    public final MainOptions options;

    @Getter
    public final Set<Rule> rules;

    @Getter
    public final PrintStream stdout;

    @Getter
    public final PrintStream stderr;

    private boolean success;

    public Test(MainOptions options, Set<Rule> rules, PrintStream stdout, PrintStream stderr) {
        this.options = options;
        this.rules = rules;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public void fail() {
        success = false;
    }

    public boolean isFailed() {
        return !success;
    }

    public int run() {
        validateRuleUniqueness(rules);
        validateRulePatterns(rules);

        return isFailed() ? 1 : 0;
    }

    private void validateRuleUniqueness(Set<Rule> rules) {
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

    private void validateRulePatterns(Set<Rule> rules) {
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

            {
                int exampleIndex = 1;
                for (var example:rule.examples) {
                    if(example.before != null) {
                        tests++;
                        try {
                            if (!rule.patterns.stream().anyMatch((pattern) -> testPattern(pattern, example.before, true))) {
                                stdout.println(ConsoleColors.red("  " + rule.id) + ":\tbefore of " + Libs.ordinalize(exampleIndex) + " example didn't match with any pattern");
                                falseNegatives += 1;
                            }
                        } catch (Exception e) {
                            errors += 1;
                            stdout.println("  " + ConsoleColors.red(rule.id) + ":\tParsing failed on before of " + Libs.ordinalize(exampleIndex) + " example");
                        }

                        try {
                            stdout.println("[DEBUG]" + "example.after: " + example.after);
                            if (!rule.patterns.stream().allMatch((pattern) -> testPattern(pattern, example.after, false))) {
                                stdout.println(ConsoleColors.red("  " + rule.id) + ":\tafter of " + Libs.ordinalize(exampleIndex) + " example matched with some of patterns");
                                falsePositives += 1;
                            }
                        } catch (Exception e) {
                            errors += 1;
                            stdout.println("  " + ConsoleColors.red(rule.id) + ":\tParsing on after of " + Libs.ordinalize(exampleIndex) + " example");
                        }
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
