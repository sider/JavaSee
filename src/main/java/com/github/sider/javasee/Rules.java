package com.github.sider.javasee;

import java.io.PrintStream;
import java.util.List;

public class Rules {
    public final String configPath;
    public final List<String> ids;
    public final PrintStream stdout;

    public Rules(String configPath, List<String> ids) {
        this(configPath, ids, System.out);
    }

    public Rules(String configPath, List<String> ids, PrintStream stdout) {
        this.configPath = configPath;
        this.ids = ids;
        this.stdout = stdout;
    }
}
