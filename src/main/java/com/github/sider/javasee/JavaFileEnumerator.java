package com.github.sider.javasee;

import com.github.javaparser.JavaParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

@AllArgsConstructor
@Getter
public class JavaFileEnumerator {
    public final List<File> paths;
    public final Config config;

    private void loadScript(File path, BiConsumer<File, JavaFile> block) {
        JavaParser parser = new JavaParser();
        var script = new JavaFile(path, () -> new JavaParser());
        block.accept(path, script);
    }

    private void enumerateFilesInDirectory(File path, Set<File> visit, BiConsumer<File, JavaFile> block) {
        if (visit.contains(path)) {
            return;
        }
        visit.add(path);

        if(path.isDirectory()) {
            if(path.getName().equals("build")) return;
            for(File child:path.listFiles()) {
                enumerateFilesInDirectory(child, visit, block);
            }
        } else if(path.getName().endsWith(".java")) {
            loadScript(path, block);
        }
    }

    public void forEach(BiConsumer<File, JavaFile> block) {
        for(File path:paths) {
            if(path.isDirectory()) {
                enumerateFilesInDirectory(path, new HashSet<>(), block);
            }
        }
    }
}
