package com.github.kmizu.java_see;

import com.github.javaparser.ast.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.File;

@AllArgsConstructor
@Getter
@ToString
public class Script {
    public final File path;
    public final Node node;

    public NodePair rootPair() {
        return new NodePair(node, null);
    }
}
