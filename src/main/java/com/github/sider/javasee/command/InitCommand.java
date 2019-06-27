package com.github.sider.javasee.command;

import com.github.sider.javasee.JavaSee;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InitCommand implements CLICommand {
    public final String TEMPLATE_RESOURCE_NAME = "template.yml";
    public final String DESTINATION_CONFIG_PATH = "javasee.yml";

    @Option(name = "-config", aliases = "--config", metaVar = "<path>", usage = "Configuration path", help = true)
    public Path configPath = Paths.get(DESTINATION_CONFIG_PATH);

    @Override
    public String getName() {
        return "init";
    }

    @Override
    public JavaSee.ExitStatus start(PrintStream out, PrintStream err) {
        var template = ClassLoader.getSystemResourceAsStream(TEMPLATE_RESOURCE_NAME);
        Path path = configPath != null ? configPath : Paths.get(DESTINATION_CONFIG_PATH);
        try {
            Files.copy(template, path);
        } catch (IOException exn) {
            throw new RuntimeException(exn);
        }
        return JavaSee.ExitStatus.OK;
    }
}
