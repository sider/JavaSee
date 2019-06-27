package com.github.sider.javasee.command;

import com.github.sider.javasee.JavaSee;

import java.io.PrintStream;
import java.util.List;


public class HelpCommand implements CLICommand {
    private List<CLICommand> commands;
    private String commandName;

    public HelpCommand(List<CLICommand> commands, String commandName) {
        this.commands = commands;
        this.commandName = commandName;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public JavaSee.ExitStatus start(PrintStream out, PrintStream err) {
        out.println(String.format("Usage: %s <command>", commandName));
        out.println("  Where command is one of:");
        for (var command : commands) {
            out.println(String.format("    %s", command.getName()));
        }
        return JavaSee.ExitStatus.OK;
    }
}
