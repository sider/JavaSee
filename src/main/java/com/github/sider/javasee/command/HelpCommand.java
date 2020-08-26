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
        out.println("  Status code:");
        out.println("    " + "OK (" + JavaSee.ExitStatus.OK.getInt() + "): " +  "The program has terminated successfully");
        out.println("    " + "ERROR (" + JavaSee.ExitStatus.ERROR.getInt() + "): " +  "The program has encountered some errors");
        out.println("    " + "FAILURE (" + JavaSee.ExitStatus.FAILURE.getInt() + "): " + "The program has detected some issues in your Java programs");
        out.println("    " + "CONFIG_FILE_NOT_FOUND (" + JavaSee.ExitStatus.CONFIG_FILE_NOT_FOUND.getInt() + "): " +  "Config file (.yaml) is not found");
        out.println("    " + "CONFIG_FILE_SYNTAX_ERROR (" + JavaSee.ExitStatus.CONFIG_FILE_SYNTAX_ERROR.getInt() + "): " +  "Config file (.yaml) has syntax errors");
        out.println("    " + "CONFIG_FILE_SCHEMA_ERROR (" + JavaSee.ExitStatus.CONFIG_FILE_SCHEMA_ERROR.getInt() + "): " +  "Config file (.yaml) has schema errors");
        out.println("    " + "CONFIG_FILE_UNKNOWN_ERROR (" + JavaSee.ExitStatus.CONFIG_FILE_UNKNOWN_ERROR.getInt() + "): " +  "Config file (.yaml) has unknown errors");
        return JavaSee.ExitStatus.OK;
    }
}
