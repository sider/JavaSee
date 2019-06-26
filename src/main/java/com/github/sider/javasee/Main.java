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
        List<String> keys = new ArrayList<>();

        Map<String, CLICommand> map = Map.of(
                "init", new InitCommand(),
                "check", new CheckCommand(),
                "find", new FindCommand(),
                "test", new TestCommand(),
                "version", new VersionCommand(),
                "help", new HelpCommand(keys)
        );

        keys.addAll(map.keySet());

        String name = args.length > 0 ? args[0] : null;
        String[] rest = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[] {};

        var command = name != null ? map.get(name) : null;
        if (command == null) {
            command = new HelpCommand(keys);
        }

        CmdLineParser parser = new CmdLineParser(command);

        try {
            parser.parseArgument(rest);
            return command;
        } catch (CmdLineException e) {
            printCommandUsage(parser, name, command);
            throw e;
        }
    }

    private void printCommandUsage(CmdLineParser parser, String commandName, CLICommand command) {
        out.print(String.format("Usage: %s", commandName));
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
