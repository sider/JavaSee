package com.github.sider.javasee.command;

import com.github.sider.javasee.*;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CheckCommand implements CLICommand {
    @Option(name = "-config", aliases = "--config", metaVar = "<config>", usage = "config YAML file", help = true)
    public String optionConfig = "javasee.yml";

    @Option(name = "-root", aliases = "--root", metaVar = "<root>", usage= "root directory", help = true)
    public String optionRoot;

    @Option(name = "-format", aliases = "--format", metaVar = "<format>", usage = "output format", help = true)
    public String optionFormat = "text";

    @Argument
    public List<String> paths = new ArrayList();

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public JavaSee.ExitStatus start(PrintStream out, PrintStream err) {
        Formatters.AbstractFormatter formatter;
        switch(optionFormat) {
            case "text":
                formatter = new Formatters.TextFormatter(out, err);
                break;
            case "json":
                formatter = new Formatters.JSONFormatter(out, err);
                break;
            default:
                throw new RuntimeException(String.format("Unknown format specified: `%s`", optionFormat));
        }
        formatter.onStart();

        try {
            if(!configPath().isFile()) {
                out.println("Configuration file " + configPath() + " does not look a file.");
                out.println("Specify configuration file by -config option");
                return JavaSee.ExitStatus.ERROR;
            }
            var rootPath = Optional.ofNullable(optionRoot).map((root) -> new File(root)).orElse(configPath().getParentFile());
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
            var result = new Object() {
                JavaSee.ExitStatus value = JavaSee.ExitStatus.OK;
            };

            new JavaFileEnumerator(paths.isEmpty() ? List.of(new File(".")) : paths.stream().map(p -> new File(p)).collect(Collectors.toList()),  config).forEach((path , script) -> {
                analyzer.javaFiles.add(script);
                formatter.onScriptLoaded(script);
            });

            analyzer.run((t) -> {
                var script = t._1;
                var rule = t._2;
                var pair = t._3;
                formatter.onIssueFound(script, rule, pair);
                result.value = JavaSee.ExitStatus.FAILURE;
            });

            return result.value;
        } catch (Exception e) {
            formatter.onFatalError(e);
            return JavaSee.ExitStatus.ERROR;
        } finally {
            formatter.onFinish();
        }
    }

    private File configPath() {
        return new File(optionConfig);
    }
}
