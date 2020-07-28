package com.github.sider.javasee;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;

@ToString
public class JavaFile {
    private final Supplier<JavaParser> parserSupplier;
    public final File path;

    public JavaFile(File path, Supplier<JavaParser> parserSupplier) {
        this.path = path;
        this.parserSupplier = parserSupplier;
    }

    public synchronized Node parseFile() {
        var parser = this.parserSupplier.get();
        try {
            var content = Files.readString(Paths.get(path.toURI()));
            return parser.parse(content).getResult().get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NodePair rootPair() {
        return new NodePair(parseFile(), null);
    }
}
