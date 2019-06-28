package com.github.sider.javasee;

import com.google.common.io.Files;

import java.io.File;
import java.util.function.Consumer;

public class TestHelper {
    public static void mkTmpDir(Consumer<File> k) {
        var dir = Files.createTempDir();
        k.accept(dir);
    }
}
