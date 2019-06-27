package com.github.sider.javasee.command;

import com.github.sider.javasee.Analyzer;
import com.github.sider.javasee.JavaFileEnumerator;
import com.github.sider.javasee.JavaSee;
import com.github.sider.javasee.StacktraceFormatting;
import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.lib.Libs;
import com.github.sider.javasee.parser.JavaSeeParser;
import com.github.sider.javasee.parser.ParseException;
import lombok.ToString;
import org.kohsuke.args4j.Argument;

import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.sider.javasee.lib.ConsoleColors.blue;
import static com.github.sider.javasee.lib.ConsoleColors.brightBlue;

@ToString
public class FindCommand implements CLICommand, StacktraceFormatting {
    @Argument(required = true, index = 0, metaVar = "<pattern>", usage = "ast pattern in <path> ...")
    public String optionPattern = null;

    @Argument(index = 1, metaVar = "<path>", usage = "paths")
    public List<String> optionPaths = new ArrayList<>();

    public AST.Expression pattern;
    public List<File> paths;

    private Analyzer analyzer;

    @Override
    public String getName() {
        return "find";
    }

    @Override
    public JavaSee.ExitStatus start(PrintStream out, PrintStream err) {
        try {
            this.pattern = new JavaSeeParser(new StringReader(optionPattern)).WholeExpression();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if(this.optionPaths.size() == 0) {
            this.optionPaths.add(".");
        }
        this.paths = optionPaths.stream().map((path) -> new File(path)).collect(Collectors.toList());
        getAnalyzer().find(pattern, (script, pair) -> {
            var path = script.path;
            var range = pair.node.getRange().get();
            var lineNumber = range.begin.line;
            var startColumn = range.begin.column;
            var endColumn = range.end.column;
            var src = getLine(path, lineNumber);
            src = blue(src.substring(0, startColumn - 1)) + brightBlue(src.substring(startColumn - 1, endColumn)) + blue(src.substring(endColumn, src.length()));

            out.println("  " + path+ ":" + lineNumber + ":" + startColumn + "\t" + src);
        });

        return JavaSee.ExitStatus.OK;
    }

    /**
     * Note that line is 1-origin
     */
    private String getLine(File path, int line) {
        var lines = Libs.wrapException(() -> Files.readAllLines(path.toPath()));
        return lines.get(line - 1);
    }

    private Analyzer getAnalyzer() {
        if(analyzer != null) return analyzer;
        this.analyzer = new Analyzer(null, null, new ArrayList<>());

        new JavaFileEnumerator(paths, null).forEach((path, script) -> {
            this.analyzer.javaFiles.add(script);
        });

        return this.analyzer;
    }
}
