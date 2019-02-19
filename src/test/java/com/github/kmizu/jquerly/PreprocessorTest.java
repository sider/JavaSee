package com.github.kmizu.jquerly;

import org.buildobjects.process.StartupException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class PreprocessorTest {
    String withTemporaryFile(String content, Function<File, String> block) {
        return Libs.wrapException(() -> {
                    File temporaryFile = File.createTempFile("jquerly-preprocessor", "");
                    try (var writer = new PrintWriter(new FileWriter(temporaryFile))) {
                        writer.print(content);
                        writer.flush();
                        return block.apply(temporaryFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
        });
    }

    @Test
    public void testProprocessingSucceeded() {
        var preprocessor = new Preprocessor(".foo", new String[]{"cat", "-n"});
        var target = withTemporaryFile("foo\nbar\n",  (path) -> {
            return preprocessor.run(path);
        });
        assertEquals("     1\tfoo\n     2\tbar\n", target);
    }

    @Test
    public void testPreprocessingFailed() {
        Exception e = Assertions.assertThrows(RuntimeException.class, () -> {
            var preprocessor = new Preprocessor(".foo", new String[]{"grep XYZ"});
            withTemporaryFile("foo\nbar\n", path -> {
                return preprocessor.run(path);
            });
        });
        assertEquals(StartupException.class, e.getCause().getClass());
    }
}
