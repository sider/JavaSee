package com.github.sider.javasee.command;

import java.io.PrintStream;

public interface CLICommand {
    boolean start(PrintStream out, PrintStream err);
}
