package com.github.sider.java_see;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.buildobjects.process.ProcBuilder;

import java.io.*;

@AllArgsConstructor
@Getter
@ToString
public class Preprocessor {
    @AllArgsConstructor
    @Getter
    @ToString
    public static class PreprocessorError extends RuntimeException {
        public final String command;
        public final int status;
    }

    public final String ext;
    public final String[] commands;

    private String[] subArray(String[] input, int inclusiveStart, int exclusiveEnd) {
        var newArray = new String[exclusiveEnd- inclusiveStart];
        for(int i = inclusiveStart; i < exclusiveEnd; i++) {
            newArray[i - inclusiveStart] = input[inclusiveStart];
        }
        return newArray;
    }

    private String join(String[] elements) {
        if(elements.length == 0) {
            return "";
        } else {
            var result = new StringBuffer();
            result.append(elements[0]);
            result.append(" ");
            for(int i = 1; i < elements.length; i++) {
                result.append(elements[i]);
            }
            return new String(result);
        }
    }

    public String run(File path) {
        try(
                var pipedInputStream = new PipedInputStream();
        ) {
            var stdOutReader = new BufferedReader(new InputStreamReader(pipedInputStream));
            var stringWriter = new StringWriter();
            var output = new PrintWriter(stringWriter);
            var reader = new Thread() {
                @Override public void run() {
                    String line;
                    try {
                        while((line = stdOutReader.readLine()) != null) {
                            output.println(line);
                        }
                    } catch(IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            var result = new ProcBuilder(commands[0], subArray(commands, 1, commands.length))
                    .withInputStream(new FileInputStream(path)).run();
            try (
                    var pipedOutputStream = new BufferedOutputStream(new PipedOutputStream(pipedInputStream));
            ) {
                pipedOutputStream.write(result.getOutputBytes());
            }
            reader.start();
            reader.join();

            if(result.getExitValue() != 0) {
                throw new PreprocessorError(join(commands), result.getExitValue());
            }

            return new String(stringWriter.getBuffer());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /*
    class Preprocessor

    def run!(path)
    stdout_read, stdout_write = IO.pipe

            output = ""

    reader = Thread.new do
            while (line = stdout_read.gets)
    output << line
            end
    end

            succeeded = system(command, in: path.to_s, out: stdout_write)
    stdout_write.close

    reader.join

    raise Error.new(status: $?, command: command) unless succeeded

    output
    end
    */
}
