package com.github.sider.javasee;

import com.github.sider.javasee.lib.Extentions;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface StacktraceFormatting {
    default String formatStacktrace(StackTraceElement[] stacktrace, int indent) {
        return Arrays.asList(stacktrace)
                .stream()
                .map((x) -> Extentions.repeat(" ", indent) + x)
                .collect(Collectors.joining("\n"));
    }
}
