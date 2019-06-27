package com.github.sider.javasee.command;

import com.github.sider.javasee.JavaSee;

import java.io.PrintStream;

public interface CLICommand {
    JavaSee.ExitStatus start(PrintStream out, PrintStream err);
    String getName();
}
