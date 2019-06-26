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
import java.util.List;

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

    public void run(String[] args) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            if(command.start(this.out, this.err)) {
                System.exit(0);
            } else {
                System.exit(-1);
            }
        } catch (CmdLineException e) {
            var help = new HelpCommand();

            // Note that it is workaround to show appropriate error message.
            // it is based on implementation detail of args4j

            // any sub-command is not given
            if(parser == e.getParser()) {
                help.start(this.out, this.err);
                System.exit(-1);
            } // parsing failure of sub-command
            else {
                help.setCmdLineException(e);
                help.start(this.out, this.err);
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) throws CmdLineException {
        new Main(System.out, System.err).run(args);
    }
}
