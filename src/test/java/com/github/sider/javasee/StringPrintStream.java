package com.github.sider.javasee;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

class StringPrintStream {
    private ByteArrayOutputStream arrayStream;
    private PrintStream stream;

    public StringPrintStream() {
        arrayStream = new ByteArrayOutputStream();
        this.stream = new PrintStream(arrayStream, true, StandardCharsets.UTF_8);
    }

    public PrintStream getStream() {
        return stream;
    }

    public String getString() {
        this.stream.flush();
        return new String(arrayStream.toByteArray(), StandardCharsets.UTF_8);
    }
}
