package com.github.sider.java_see;

import com.github.javaparser.JavaParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

@AllArgsConstructor
@Getter
public class ScriptEnumerator {
    public final List<File> paths;
    public final Config config;

    private void loadScript(File path, BiConsumer<File, Script> block) {
        try {
            var content = Files.readString(Paths.get(path.toURI()));
            JavaParser parser = new JavaParser();
            var script = new Script(path, parser.parse(content).getResult().get());
            block.accept(path, script);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void enumerateFilesInDirectory(File path, Set<File> visit, BiConsumer<File, Script> block) {
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

    public void forEach(BiConsumer<File, Script> block) {
        System.out.println(paths);
        for(File path:paths) {
            if(path.isDirectory()) {
                enumerateFilesInDirectory(path, new HashSet<>(), block);
            }
        }
    }
}
