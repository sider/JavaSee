package com.github.sider.java_see;

import com.github.sider.java_see.ast.AST;
import com.github.sider.java_see.libs.ConsoleColors;
import com.github.sider.java_see.libs.Libs;
import com.github.sider.java_see.parser.JavaSeeParser;
import com.github.sider.java_see.parser.ParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;

import static com.github.sider.java_see.libs.ConsoleColors.*;

@ToString
public class Find implements StacktraceFormatting {
    public final String patternString;
    public final AST.Expression pattern;
    public final List<File> paths;
    private Analyzer analyzer;

    public Find(String patternString, List<File> paths) {
        this.patternString = patternString;
        try {
            this.pattern = new JavaSeeParser(new StringReader(patternString)).Expression();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.paths = paths;
    }

    public void start() {
        var count = 0;
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

        new ScriptEnumerator(paths, null).forEach((path, script) -> {
            this.analyzer.scripts.add(script);
        });

        return this.analyzer;
    }
}
