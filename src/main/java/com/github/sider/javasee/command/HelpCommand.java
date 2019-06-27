package com.github.sider.javasee.command;

import java.io.PrintStream;
import java.util.List;


public class HelpCommand implements CLICommand {
    private List<CLICommand> commands;

    public HelpCommand(List<CLICommand> commands) {
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public boolean start(PrintStream out, PrintStream err) {
        out.println("Usage: javasee <command>");
        out.println("  Where command is one of:");
        for (var command : commands) {
            out.println(String.format("    %s", command.getName()));
        }
        return true;
    }
}
