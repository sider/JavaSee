package com.github.sider.javasee.command;

import com.github.sider.javasee.*;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
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
        File configPath = configPath();
        Formatters.AbstractFormatter formatter;
        switch(optionFormat) {
            case "text":
                formatter = new Formatters.TextFormatter(out, err);
                break;
            case "json":
                formatter = new Formatters.JSONFormatter(out, err);
                break;
            default:
                throw new Exceptions.UnknownFormatException(optionFormat);
        }
        formatter.onStart();

        try {
            if(!configPath.isFile() || !configPath.exists()) {
                err.println("Configuration file " + configPath + " does not look a file.");
                err.println("Specify configuration file by -config option.");
                return JavaSee.ExitStatus.CONFIG_FILE_NOT_FOUND;
            }
            var rootPath = Optional.ofNullable(optionRoot).map((root) -> new File(root)).orElse(configPath.getParentFile());
            Map<String, Object> yaml;
            Config config = null;
            try {
                yaml = new Yaml().load(new FileInputStream(configPath));
                if(yaml == null) {
                    err.println("YAML file \"" + configPath + "\" has unknown error");
                    return JavaSee.ExitStatus.CONFIG_FILE_UNKNOWN_ERROR;
                }
                config = Config.load(configPath, rootPath);
            } catch (Exceptions.YamlValidationException e) {
                err.println("YAML file \"" + e.configPath + "\" has schema error: " + e.getMessage());
                return JavaSee.ExitStatus.CONFIG_FILE_SCHEMA_ERROR;
            } catch (FileNotFoundException e) {
                err.println("YAML file is not found: " + e.getMessage());
                return JavaSee.ExitStatus.CONFIG_FILE_NOT_FOUND;
            } catch (YAMLException e) {
                err.println("YAML file \"" + configPath + "\" has syntax error: " + e.getMessage());
                return JavaSee.ExitStatus.CONFIG_FILE_SYNTAX_ERROR;
            } catch (Exceptions.ConfigFileException e) {
                err.println(e.getMessage());
                return e.status;
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
