package com.github.sider.javasee.command;

import lombok.Getter;
import lombok.Setter;
import org.kohsuke.args4j.CmdLineException;

import java.io.PrintStream;


public class HelpCommand implements CLICommand {
    /*
     * Hacks to show better message
     */
    @Getter
    @Setter
    private CmdLineException cmdLineException;

    @Override
    public boolean start(PrintStream out, PrintStream err) {
        if(cmdLineException!= null) {
            out.println(cmdLineException.getMessage());
            out.println("Usage: ");
            out.println();
            cmdLineException.getParser().printSingleLineUsage(System.out);
            out.println();
            cmdLineException.getParser().printUsage(System.out);
            out.println();
        } else {
            out.println("Usage: java -jar JavaSee-all.jar <command> [<args>]");
            out.println();
            out.println("These are JavaSee commands:");
            out.println();
            out.println("  version       Show current version of JavaSee");
            out.println();
            out.println("  help          Show help message");
            out.println();
            out.println("  init          Generate the default config file under current directory");
            out.println();
            out.println("  check         Find source files matching with given patterns using specified config file");
            out.println();
            out.println("  find          Find source files matching with a given pattern using command line args");
            out.println();
            out.println("  test          Find source files matching and not-matching with given pattern using specified config file");
        }
        return true;
    }
}
