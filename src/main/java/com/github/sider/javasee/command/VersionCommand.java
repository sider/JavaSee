package com.github.sider.javasee.command;

import com.github.sider.javasee.Main;
import com.github.sider.javasee.Version;
import lombok.Getter;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

import java.io.PrintStream;

public class VersionCommand implements CLICommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public boolean start(PrintStream out, PrintStream err) {
        out.println("JavaSee " + Version.VERSION);
        return true;
    }
}
