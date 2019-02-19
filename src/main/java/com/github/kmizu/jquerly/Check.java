package com.github.kmizu.jquerly;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class  Check {

    @Option(name="-config")
    private String config;

    @Option(name="-root")
    private String root;

    @Option(name="-format")
    private String format;

    @Option(name="-rule")
    private String rule;

    private File configPath() {
        var path = new File(config);
        if(path.isFile()) return path;
        return new File("querly.yaml");
    }

    public String getConfig() {
        return config;
    }

    public String getRoot() {
        return root;
    }

    public String getFormat() {
        return format;
    }

    public String getRule() {
        return rule;
    }

    public final CmdLineParser parser;

    public Check() {
        parser = new CmdLineParser(this);
    }

    private void println(String line) {
        System.err.println(line);
    }

    public void run(String[] args) throws CmdLineException {
        parser.parseArgument(args);
        println(config);
        println(root);
        println(format);
        println(rule);
    }

    public static void main(String[] args) throws CmdLineException {
        new Check().run(args);
    }

    /**
     * Check paths based on configuration
     * @param paths
     */
    private void check(String... paths) {
        if(config == null) {
            config = "querly.yml";
        }
        if(format == null) {
            format = "text";
        }
    }


    /*
    desc "check [paths]", "Check paths based on configuration"
    option :config, default: "querly.yml"
    option :root
    option :format, default: "text", type: :string, enum: %w(text json)
    option :rule, type: :string
    def check(*paths)
      require 'querly/cli/formatter'

      formatter = case options[:format]
                  when "text"
                    Formatter::Text.new
                  when "json"
                    Formatter::JSON.new
                  end
      formatter.start

      begin
        unless config_path.file?
          STDERR.puts <<-Message
Configuration file #{config_path} does not look a file.
Specify configuration file by --config option.
          Message
          exit 1
        end

        root_option = options[:root]
        root_path = root_option ? Pathname(root_option).realpath : config_path.parent.realpath

        config = begin
          yaml = YAML.load(config_path.read)
          Config.load(yaml, config_path: config_path, root_dir: root_path, stderr: STDERR)
        rescue => exn
          formatter.config_error config_path, exn
          exit 1
        end

        analyzer = Analyzer.new(config: config, rule: options[:rule])

        ScriptEnumerator.new(paths: paths.empty? ? [Pathname.pwd] : paths.map {|path| Pathname(path) }, config: config).each do |path, script|
          case script
          when Script
            analyzer.scripts << script
            formatter.script_load script
          when StandardError, LoadError
            formatter.script_error path, script
          end
        end

        analyzer.run do |script, rule, pair|
          formatter.issue_found script, rule, pair
        end
      rescue => exn
        formatter.fatal_error exn
        exit 1
      ensure
        formatter.finish
      end
    end
    */

    /**
     * This is a subcommand method.
     * Find for the pattern in given paths
     * @param pattern
     * @param paths
     */
    private void find(String pattern, String... paths) {

    }

    /*
    desc "find pattern [paths]", "Find for the pattern in given paths"
    def find(pattern, *paths)
      require 'querly/cli/find'

      Find.new(
        pattern: pattern,
        paths: paths.empty? ? [Pathname.pwd] : paths.map {|path| Pathname(path) },
      ).start
    end
    */

    /**
     * This is a subcommand method.
     * Check configuration
     */
    private void test() {
        config = "querly.yml";
    }
    /*
    desc "test", "Check configuration"
    option :config, default: "querly.yml"
    def test()
      require "querly/cli/test"
      exit Test.new(config_path: config_path).run
    end
    */

    /**
     * This is a subcommand method.
     * Print loaded rules
     */
    private void rules() {
        config = "querly.yml";
    }

    /*
    desc "rules", "Print loaded rules"
    option :config, default: "querly.yml"
    def rules(*ids)
      require "querly/cli/rules"
      Rules.new(config_path: config_path, ids: ids).run
    end
    */

    /**
     * This is a subcommand method.
     * Print version
     */
    private void version() {
        System.out.println("jquerly " + Version.VERSION);
    }


    /**
     * This is a subcommand method.
     * Generate Querly config file (querly.yml)
     */
    private void init() throws IOException  {
        Files.copy(Paths.get("template.yml"), Paths.get("querly.yml"));
    }

    /*
    def self.source_root
      File.join(__dir__, "../..")
    end

    include Thor::Actions

    desc "init", "Generate Querly config file (querly.yml)"
    def init()
      copy_file("template.yml", "querly.yml")
    end

end
            */
}
