package com.github.sider.javasee.lib;

public class ConsoleColors {
    public static String red(String src) {
        return "\033[31m" + src + "\033[0m";
    }

    public static String green(String src) {
        return "\033[32m" + src + "\033[0m";
    }

    public static String blue(String src) {
        return "\033[34m" + src + "\033[0m";
    }

    public static String brightBlue(String src) {
        return "\033[36m" + src + "\033[0m";
    }
}
