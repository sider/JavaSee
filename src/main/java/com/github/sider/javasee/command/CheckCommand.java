package com.github.sider.javasee.command;

import com.github.sider.javasee.Analyzer;
import com.github.sider.javasee.Config;
import com.github.sider.javasee.Formatters;
import com.github.sider.javasee.JavaFileEnumerator;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CheckCommand implements CLICommand {
    private PrintStream sysout = System.out;
    private PrintStream syserr = System.err;

    @Option(name = "-config", aliases = "--config", metaVar = "<config>", usage = "config YAML file", help = true)
    public String optionConfig = "javasee.yml";

    @Option(name = "-root", aliases = "--root", metaVar = "<root>", usage= "root directory", help = true)
    public String optionRoot;

    @Option(name = "-format", aliases = "--format", metaVar = "<format>", usage = "output format", help = true)
    public String optionFormat = "text";

    @Argument
    private List<String> paths = List.of();

    @Override
    public boolean start() {
        Formatters.AbstractFormatter formatter;
        switch(optionFormat) {
            case "text":
                formatter = new Formatters.TextFormatter();
                break;
            case "json":
                formatter = new Formatters.JSONFormatter();
                break;
            default:
                throw new RuntimeException(String.format("Unknown format specified: `%s`", optionFormat));
        }
        formatter.onStart();

        try {
            if(!configPath().isFile()) {
                sysout.println("Configuration file " + configPath() + " does not look a file.");
                sysout.println("Specify configuration file by -config option");
                return false;
            }
            File rootPath;
            if(optionRoot != null) {
                rootPath = new File(optionRoot);
            } else {
                rootPath = configPath().getParentFile();
            }
            Map<String, Object> yaml;
            Config config = null;
            try {
                yaml = new Yaml().load(new FileInputStream(configPath()));
                config = Config.load(yaml, configPath(), rootPath);
            } catch (Exception e) {
                e.printStackTrace();
                formatter.onConfigError(optionConfig, e);
            }

            var analyzer = new Analyzer(config, optionRoot, new ArrayList<>());

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
        return false;
    }

    private File configPath() {
        return new File(optionConfig);
    }
}
