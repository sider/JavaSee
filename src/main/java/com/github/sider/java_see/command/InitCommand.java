package com.github.sider.java_see.command;

import com.github.sider.java_see.Main;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InitCommand extends CLICommand {
    public final String templateResourceName;
    public final Path destinationPath;

    public InitCommand(Main.Options options, String templateResourceName, Path destinationPath) {
        super(options);
        this.templateResourceName = templateResourceName;
        this.destinationPath = destinationPath;
    }

    @Override
    public boolean start () {
        try {
            var template = ClassLoader.getSystemResourceAsStream(templateResourceName);
            Files.copy(template, destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
