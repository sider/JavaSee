package com.github.sider.javasee.command;

import com.github.sider.javasee.Main;
import com.github.sider.javasee.Version;
import lombok.Getter;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

import java.io.PrintStream;

public class VersionCommand implements CLICommand {
    @Option(name = "-help", aliases = "--help", handler = BooleanOptionHandler.class)
    @Getter
    private boolean helpRequired;

    private final PrintStream out = System.out;
    private final PrintStream err = System.out;

    @Override
    public boolean start() {
        out.println("JavaSee " + Version.VERSION);
        return true;
    }
}
