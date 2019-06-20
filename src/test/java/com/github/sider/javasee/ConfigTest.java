package com.github.sider.javasee;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.parser.JavaSeeParser;
import com.github.sider.javasee.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {
    private AST.Expression parse(String input) {
        try {
            return new JavaSeeParser(new StringReader(input)).WholeExpression();
        } catch (ParseException e) {
            return null;
        }
    }

    @Test
    public void testLoadYAML() {
        var yaml = "rules:\n" +
                "  - id: greeting\n" +
                "    message: |\n" +
                "      Hello world\n" +
                "      \n" +
                "      Lorem ipsum.\n" +
                "    pattern: _.println(...)\n";
        Map<String, Object> map = new Yaml().load(yaml);

        var config = Config.load(map,  new File("foo.yml"), new File("foo/bar/baz"));
        assertEquals(1, config.rules.size());

        var rule = config.rules.get(0);
        assertNotNull(rule);

        assertEquals("greeting", rule.id);
        assertEquals(Arrays.asList("Hello world\n\nLorem ipsum.\n"), rule.messages);

        for (var pattern: rule.patterns) {
            assertTrue(pattern instanceof AST.MethodCall);
        };
    }
}
