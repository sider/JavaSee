package com.github.sider.javasee.command;

import com.github.sider.javasee.Main;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.args4j.CmdLineException;


public class HelpCommand implements CLICommand {
    @Override
    public boolean isHelpRequired() {
        return true;
    }

    /*
     * Hacks to show better message
     */
    @Getter
    @Setter
    private CmdLineException cmdLineException;

    private void println(String message) {
        System.out.println(message);
    }
    private void println() {
        System.out.println();
    }
    private void print(String message) {
        System.out.print(message);
    }

    @Override
    public boolean start() {
        if(cmdLineException!= null) {
            println(cmdLineException.getMessage());
            println("Usage: ");
            println();
            cmdLineException.getParser().printSingleLineUsage(System.out);
            println();
            cmdLineException.getParser().printUsage(System.out);
            println();
        } else {
            println("Usage: java -jar JavaSee-all.jar <command> [<args>]");
            println();
            println("These are JavaSee commands:");
            println();
            println("  version       Show current version of JavaSee");
            println();
            println("  help          Show help message");
            println();
            println("  init          Generate the default config file under current directory");
            println();
            println("  check         Find source files matching with given patterns using specified config file");
            println();
            println("  find          Find source files matching with a given pattern using command line args");
            println();
            println("  test          Find source files matching and not-matching with given pattern using specified config file");
        }
        return true;
    }
}
