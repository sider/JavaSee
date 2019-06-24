package com.github.sider.javasee;

import com.github.sider.javasee.command.*;
import lombok.Getter;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    @Getter
    private final List<Class> commandClasses= List.of(
            VersionCommand.class,
            CheckCommand.class,
            FindCommand.class,
            InitCommand.class,
            TestCommand.class,
            HelpCommand.class
    );
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

    public void run(String[] args) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            var arguments = e.getParser().getArguments();

            System.out.println(arguments.stream().map(a -> a.getClass()).collect(Collectors.toList()));

            var help = new HelpCommand();
            // Note that it is workaround to show appropriate error message.
            // it is based on implementation detail of args4j
            //
            // arguments contains SubCommandHandler ==> no arguments are given
            // arguments contains OptionHandler ==> parsing failure of sub-command
            //
            if(arguments.stream().anyMatch(arg -> arg instanceof SubCommandHandler)) {
                help.setGivenErrorMessage(e.getMessage());
            } else if(arguments.stream().anyMatch(arg -> arg instanceof OptionHandler)) {
                help.setGivenErrorMessage(e.getMessage());
            }

            help.start();
            System.exit(-1);
        }
        if(command.start()) {
            System.exit(0);
        } else {
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws CmdLineException {
        new Main().run(args);
    }
}
