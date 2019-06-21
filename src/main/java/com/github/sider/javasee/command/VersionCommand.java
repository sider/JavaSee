package com.github.sider.javasee.command;

import com.github.sider.javasee.Main;
import com.github.sider.javasee.Version;

import java.io.PrintStream;

public class VersionCommand implements CLICommand {
    private final PrintStream out = System.out;
    private final PrintStream err = System.out;

    @Override
    public boolean start() {
        out.println("JavaSee " + Version.VERSION);
        return true;
    }
}
