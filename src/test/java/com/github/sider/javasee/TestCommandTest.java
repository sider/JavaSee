package com.github.sider.javasee;

import com.github.sider.javasee.command.CLICommand;
import com.github.sider.javasee.command.HelpCommand;
import com.github.sider.javasee.command.TestCommand;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestCommandTest {
    StringPrintStream stdout = new StringPrintStream();
    StringPrintStream stderr = new StringPrintStream();

    Config config(String content) {
        Map<String, Object> map = new Yaml().load(content);
        return Config.load(map,  new File("foo.yml"), new File("foo/bar/baz"));
    }

    @Test
    public void testTestUniquenessSuccess() throws Exception {
        var yaml = "rules:\n" +
                "  - id: greeting\n" +
                "    message: |\n" +
                "      Hello world\n" +
                "      \n" +
                "      Lorem ipsum.\n" +
                "    pattern: _.println(...)\n" +
                "  - id: greeting2\n" +
                "    message: |\n" +
                "      Hello world\n" +
                "      \n" +
                "      Lorem ipsum.\n" +
                "    pattern: _.println(...)\n";

        var config = config(yaml);

        TestCommand test = new TestCommand();

        test.validateRuleUniqueness(stdout.getStream(), config.rules);

        assertFalse(test.isFailed());
        assertEquals("Checking rule id uniqueness...\n", stdout.getString());
    }

    @Test
    public void testTestUniquenessFail() throws Exception {
        var yaml = "rules:\n" +
                "  - id: greeting\n" +
                "    message: |\n" +
                "      Hello world\n" +
                "      \n" +
                "      Lorem ipsum.\n" +
                "    pattern: _.println(...)\n" +
                "  - id: greeting\n" +
                "    message: |\n" +
                "      Hello world\n" +
                "      \n" +
                "      Lorem ipsum.\n" +
                "    pattern: _.println(...)\n";

        var config = config(yaml);

        TestCommand test = new TestCommand();

        test.validateRuleUniqueness(stdout.getStream(), config.rules);

        assertTrue(test.isFailed());
        assertEquals("Checking rule id uniqueness...\n" +
                "\u001B[31m  Rule id `greeting` duplicated!\u001B[0m\n", stdout.getString());
    }

    @Test
    public void testTestPatternSuccessEmpty() throws Exception {
        var yaml = "rules:\n" +
                "  - id: greeting\n" +
                "    message: |\n" +
                "      Hello world\n" +
                "      \n" +
                "      Lorem ipsum.\n" +
                "    pattern: _.println(...)\n";

        var config = config(yaml);

        TestCommand test = new TestCommand();

        test.validateRulePatterns(stdout.getStream(), config.rules);

        assertFalse(test.isFailed());
        assertEquals("Checkintg rule patterns...\n" +
                "Tested 1 rules with 0 tests\n" +
                "\u001B[32m  All tests green!\u001B[0m\n", stdout.getString());
    }

    @Test
    public void testTestPatternSuccess() throws Exception {
        var yaml = "rules:\n" +
                "  - id: greeting\n" +
                "    message: Hello world\n" +
                "    pattern: _.println(...)\n" +
                "    tests:\n" +
                "      match:\n" +
                "        - System.out.println(123)\n" +
                "        - System.err.println(true)\n" +
                "      unmatch:\n" +
                "        - System.out.print(123)\n" +
                "        - System.err.print(false)\n";

        var config = config(yaml);

        TestCommand test = new TestCommand();

        test.validateRulePatterns(stdout.getStream(), config.rules);

        assertFalse(test.isFailed());
        assertEquals("Checkintg rule patterns...\n" +
                "Tested 1 rules with 4 tests\n" +
                "\u001B[32m  All tests green!\u001B[0m\n", stdout.getString());
    }

    @Test
    public void testTestPatternFail() throws Exception {
        var yaml = "rules:\n" +
                "  - id: greeting\n" +
                "    message: Hello world\n" +
                "    pattern: _.println(...)\n" +
                "    tests:\n" +
                "      unmatch:\n" +
                "        - System.out.println(123)\n" +
                "        - System.err.println(true)\n" +
                "      match:\n" +
                "        - System.out.print(123)\n" +
                "        - System.err.print(false)\n";

        var config = config(yaml);

        TestCommand test = new TestCommand();

        test.validateRulePatterns(stdout.getStream(), config.rules);

        assertTrue(test.isFailed());
        assertEquals("Checkintg rule patterns...\n" +
                "\u001B[31m  greeting\u001B[0m:\t1st match example didn't match with any pattern\n" +
                "\u001B[31m  greeting\u001B[0m:\t2nd match example didn't match with any pattern\n" +
                "\u001B[31m  greeting\u001B[0m:\t1st unmatch example matched with some of patterns\n" +
                "\u001B[31m  greeting\u001B[0m:\t2nd unmatch example matched with some of patterns\n" +
                "Tested 1 rules with 4 tests\n" +
                "  2 examples found which should not match, but matched\n" +
                "  2 examples found which should match, but didn't\n", stdout.getString());
    }
}
