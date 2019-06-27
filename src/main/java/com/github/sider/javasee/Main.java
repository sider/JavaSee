package com.github.sider.javasee;

import com.github.sider.javasee.command.*;
import lombok.Getter;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

import java.io.PrintStream;
import java.util.*;

public class Main {
    @Argument(handler = SubCommandHandler.class, required = true, metaVar = "<command>")
    @SubCommands({
            @SubCommand(name = "version", impl = VersionCommand.class),
            @SubCommand(name = "check", impl = CheckCommand.class),
            @SubCommand(name = "find", impl = FindCommand.class),
            @SubCommand(name = "init", impl = InitCommand.class),
            @SubCommand(name = "test", impl = TestCommand.class),
            @SubCommand(name = "help", impl = HelpCommand.class)
    })
    private CLICommand command;

    private PrintStream out;
    private PrintStream err;

    public Main(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    public CLICommand parse(String[] args) throws CmdLineException {
        ArrayList<CLICommand> commands = new ArrayList<>();

        HelpCommand help = new HelpCommand(commands);
        commands.add(new InitCommand());
        commands.add(new CheckCommand());
        commands.add(new FindCommand());
        commands.add(new TestCommand());
        commands.add(new VersionCommand());
        commands.add(help);

        String name = args.length > 0 ? args[0] : null;
        String[] rest = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[] {};

        CLICommand command;
        if (name != null) {
            command = commands.stream().filter(c -> c.getName().equals(name)).findAny().orElse(help);
        } else {
            command = help;
        }

        try {
            CmdLineParser parser = new CmdLineParser(command);
            parser.parseArgument(rest);
            return command;
        } catch (CmdLineException e) {
            printCommandUsage(e.getParser(), command);
            throw e;
        }
    }

    private void printCommandUsage(CmdLineParser parser, CLICommand command) {
        out.print(String.format("Usage: %s", command.getName()));
        parser.printSingleLineUsage(out);
        out.println();
        parser.printUsage(out);
    }

    public static void main(String[] args) {
        try {
            var command = new Main(System.out, System.err).parse(args);
            if (!command.start(System.out, System.err)) {
                System.exit(-1);
            }
        } catch (CmdLineException e) {
            System.exit(-1);
        }
    }
}
