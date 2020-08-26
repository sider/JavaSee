package com.github.sider.javasee;

import com.github.sider.javasee.command.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Main {
    private PrintStream out;
    private PrintStream err;
    private String commandName;

    public Main(PrintStream out, PrintStream err, String commandName) {
        this.out = out;
        this.err = err;
        this.commandName = commandName;
    }

    public Optional<CLICommand> parse(String[] args) {
        ArrayList<CLICommand> commands = new ArrayList<>();

        HelpCommand help = new HelpCommand(commands, commandName);
        commands.add(new InitCommand());
        commands.add(new CheckCommand());
        commands.add(new FindCommand());
        commands.add(new TestCommand());
        commands.add(new VersionCommand());
        commands.add(help);

        Optional<String> nameOpt = args.length > 0 ? Optional.of(args[0]) : Optional.empty();
        String[] rest = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[] {};

        var command = nameOpt.map((name) ->
            commands.stream().filter(c -> c.getName().equals(name)).findAny().orElse(help)
        ).orElse(help);

        try {
            CmdLineParser parser = new CmdLineParser(command);
            parser.parseArgument(rest);
            return Optional.of(command);
        } catch (CmdLineException e) {
            printCommandUsage(e.getParser(), command);
            return Optional.empty();
        }
    }

    private void printCommandUsage(CmdLineParser parser, CLICommand command) {
        out.print(String.format("Usage: %s %s", commandName, command.getName()));
        parser.printSingleLineUsage(out);
        out.println();
        parser.printUsage(out);
    }

    public static void main(String[] args) {
        try {
            var commandOpt = new Main(System.out, System.err, JavaSee.getCommandLineName()).parse(args);
            var status = commandOpt.map((command) -> command.start(System.out, System.err)).orElse(JavaSee.ExitStatus.ERROR);
            System.exit(status.getInt());
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(JavaSee.ExitStatus.ERROR.getInt());
        }
    }
}
