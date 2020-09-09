package com.github.sider.javasee;

import com.github.sider.javasee.command.CheckCommand;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CheckCommandTest {
    @Test
    public void testCheckPrintln() throws Exception {
        var yaml = "rules:\n" +
                   "  - id: check-println\n" +
                   "    pattern: _.println(...)\n" +
                   "    message: |\n" +
                   "      println() is detected\n";
        var configFile = File.createTempFile("abc", "def");
        Files.writeString(configFile.toPath(), yaml);
        CheckCommand check = new CheckCommand();
        check.optionConfig = configFile.getPath();
        check.paths = List.of("src/test/resources/check");
        var out = new ByteArrayOutputStream();
        var status = check.start(new PrintStream(out), System.err);
        assertEquals(JavaSee.ExitStatus.FAILURE, status);
        assertEquals(
           "src/test/resources/check/Println.java:3:9\t\u001B[31m        System.out.println(\"test check command\");\u001B[0m\tprintln() is detected(check-println)\n",
            new String(out.toByteArray()))
        ;
    }
}
