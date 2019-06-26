package com.github.sider.javasee.command;

import lombok.Getter;
import lombok.Setter;
import org.kohsuke.args4j.CmdLineException;

import java.io.PrintStream;
import java.util.List;


public class HelpCommand implements CLICommand {
    private List<String> keys;

    public HelpCommand(List<String> keys) {
        this.keys = keys;
    }

    @Override
    public boolean start(PrintStream out, PrintStream err) {
        out.println("Usage: javasee <subcommand>");
        out.println("  subcommand ::=");
        for (var key : keys) {
            out.println(String.format("    %s", key));
        }
        return true;
    }
}
