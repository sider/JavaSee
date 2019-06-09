package com.github.sider.java_see;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.List;

public class MainOptions {
    @Option(name="-config")
    public String config;

    @Option(name="-root")
    public String root;

    @Option(name="-format")
    public String format;

    @Option(name="-rule")
    public String rule;

    @Argument
    public List<String> paths = new ArrayList<>();

    public final CmdLineParser parser = new CmdLineParser(this);
}
