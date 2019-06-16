package com.github.sider.javasee.command;

import com.github.sider.javasee.Main;
import com.github.sider.javasee.Version;

import java.io.PrintStream;

public class VersionCommand extends CLICommand {
    public final PrintStream out;
    public final PrintStream err;

    public VersionCommand(Main.Options options, PrintStream out, PrintStream err) {
        super(options);
        this.out = out;
        this.err = err;
    }

    @Override
    public boolean start() {
        out.println("JavaSee " + Version.VERSION);
        return true;
    }
}
