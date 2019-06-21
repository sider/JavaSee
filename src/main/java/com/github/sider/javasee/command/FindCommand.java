package com.github.sider.javasee.command;

import com.github.sider.javasee.Analyzer;
import com.github.sider.javasee.Main;
import com.github.sider.javasee.JavaFileEnumerator;
import com.github.sider.javasee.StacktraceFormatting;
import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.lib.Libs;
import com.github.sider.javasee.parser.JavaSeeParser;
import com.github.sider.javasee.parser.ParseException;
import lombok.ToString;
import org.kohsuke.args4j.Argument;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static com.github.sider.javasee.lib.ConsoleColors.*;

@ToString
public class FindCommand implements CLICommand, StacktraceFormatting {
    @Argument(required = true, usage = "Find for the pattern in given paths")
    public String optionPattern = null;

    @Argument
    public List<String> optionPaths = new ArrayList<>();

    public final AST.Expression pattern;
    public final List<File> paths;

    private Analyzer analyzer;

    public FindCommand() {
        try {
            this.pattern = new JavaSeeParser(new StringReader(optionPattern)).WholeExpression();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.paths = optionPaths.stream().map((path) -> new File(path)).collect(Collectors.toList());
    }

    @Override
    public boolean start() {
        getAnalyzer().find(pattern, (script, pair) -> {
            var path = script.path;
            var range = pair.node.getRange().get();
            var lineNumber = range.begin.line;
            var startColumn = range.begin.column;
            var endColumn = range.end.column;
            var src = getLine(path, lineNumber);
            src = blue(src.substring(0, startColumn - 1)) + brightBlue(src.substring(startColumn - 1, endColumn)) + blue(src.substring(endColumn, src.length()));

            System.out.println("  " + path+ ":" + lineNumber + ":" + startColumn + "\t" + src);
        });
        return true;
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
