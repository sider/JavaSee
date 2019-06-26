package com.github.sider.javasee.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InitCommand implements CLICommand {
    public final String TEMPLATE_RESOURCE_NAME = "template.yml";
    public final String DESTINATION_CONFIG_PATH = "javasee.yml";

    private Path destinationPath;

    @Override
    public boolean start () {
        try {
            var template = ClassLoader.getSystemResourceAsStream(TEMPLATE_RESOURCE_NAME);
            Files.copy(template, Paths.get(DESTINATION_CONFIG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
