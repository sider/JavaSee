package com.github.kmizu.jquerly;

import com.github.javaparser.ast.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Script {
    public final String path;
    public final Node node;

    public NodePair rootPair() {
        return new NodePair(node, null);
    }
}
