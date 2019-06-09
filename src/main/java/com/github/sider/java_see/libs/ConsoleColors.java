package com.github.sider.java_see.libs;

public class ConsoleColors {
    public static String red(String src) {
        return "\033[31m" + src + "\033[0m";
    }

    public static String green(String src) {
        return "\033[36m" + src + "\033[0m";
    }
}
