package com.github.sider.java_see;

import com.github.sider.java_see.command.FindCommand;
import com.github.sider.java_see.command.InitCommand;
import com.github.sider.java_see.command.TestCommand;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
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
    public static class Options {
        @Option(name="-config")
        public String config;

        @Option(name="-root")
        public String root;

        @Option(name="-format")
        public String format;

        @Option(name="-rule")
        public String rule;

        @Argument
        public List<String> paths = new ArrayList<>();

        public final CmdLineParser parser = new CmdLineParser(this);

        public File configPath() {
            var path = new File(config);
            if(path.isFile()) return path;
            return new File("java_see.yaml");
        }
    }

    private Options options = new Options();



    private void println(String line) {
        System.err.println(line);
    }

    private void help() {
        println("java -jar JavaSee-all.jar (check|init) ...");
    }


    public void run(String[] args) throws CmdLineException {
        if(args.length == 0) {
            help();
        } else {
            String subCommand = args[0];
            String[] tmpArgs = new String[args.length - 1];
            for(int i = 0; i < tmpArgs.length; i++) {
                tmpArgs[i] = args[i + 1];
            }
            args = tmpArgs;
            options.parser.parseArgument(args);
            switch(subCommand) {
                case "check":
                    check(options.paths);
                    break;
                case "test":
                    test();
                    break;
                case "find":
                    String pattern = args[0];
                    String[] restArgs = new String[args.length - 1];
                    for(int i = 0; i < restArgs.length; i++) {
                        restArgs[i] = args[i + 1];
                    }
                    args = restArgs;
                    find(pattern, restArgs);
                case "version":
                    version();
                    break;
                case "init":
                    init();
                    break;
            }
        }
    }

    public static void main(String[] args) throws CmdLineException {
        new Main().run(args);
    }

    /**
     * Main paths based on configuration
     * @param paths
     */
    private void check(List<String> paths) {
        if(options.config == null) {
            options.config = "java_see.yml";
        }
        if(options.format == null) {
            options.format = "text";
        }
        Formatters.AbstractFormatter formatter;
        switch(options.format) {
            case "text":
                formatter = new Formatters.TextFormatter();
                break;
            case "json":
                formatter = new Formatters.JSONFormatter();
                break;
            default:
                throw new RuntimeException("cannot reach here");
        }
        formatter.onStart();

        try {
            if(!options.configPath().isFile()) {
                println("Configuration file " + options.configPath() + " does not look a file.");
                println("Specify configuration file by -config option");
                return;
            }
            File rootPath;
            if(options.root != null) {
                rootPath = new File(options.root);
            } else {
                rootPath = options.configPath().getParentFile();
            }
            Map<String, Object> yaml;
            Config config = null;
            try {
                yaml = new Yaml().load(new FileInputStream(options.configPath()));
                config = Config.load(yaml, options.configPath(), rootPath);
            } catch (Exception e) {
                e.printStackTrace();
                formatter.onConfigError(options.config, e);
            }
            var analyzer = new Analyzer(config, options.rule, new ArrayList<>());

            new JavaFileEnumerator(paths.isEmpty() ? List.of(new File(".")) : paths.stream().map(p -> new File(p)).collect(Collectors.toList()),  config).forEach((path , script) -> {
                analyzer.javaFiles.add(script);
                formatter.onScriptLoaded(script);
            });

            analyzer.run((t) -> {
                var script = t._1;
                var rule = t._2;
                var pair = t._3;
                formatter.onIssueFound(script, rule, pair);
            });
        } catch (Exception e) {
            e.printStackTrace();
            formatter.onFatalError(e);
        } finally {
            formatter.onFinish();
        }
    }

    /**
     * This is a subcommand method.
     * FindCommand for the pattern in given paths
     * @param pattern
     * @param paths
     */
    private void find(String pattern, String... paths) {
        new FindCommand(
                options,
                pattern,
                paths.length == 0 ?
                        List.of(new File(".")) :
                        Arrays.asList(paths).stream().map((path) -> new File(path)).collect(Collectors.toList())
        ).start();
    }

    /**
     * This is a subcommand method.
     * Main configuration
     */
    private boolean test() {
        return new TestCommand(options, System.out, System.err).start();
    }

    /**
     * This is a subcommand method.
     * Print version
     */
    private void version() {
        System.out.println("JavaSee " + Version.VERSION);
    }


    /**
     * This is a subcommand method.
     * Generate JavaSee config file (java_see.yml)
     */
    private boolean init() {
        return new InitCommand(options, "template.yml", Paths.get("java_see.yml")).start();
    }
}
