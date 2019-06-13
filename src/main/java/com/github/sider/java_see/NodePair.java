package com.github.sider.java_see;

import com.github.javaparser.ast.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
@ToString
public class NodePair {
    public final Node node;
    public final NodePair parent;

    public List<NodePair> children() {
        List<NodePair> result = new ArrayList<>();
        for(Node child:node.getChildNodes()) {
            result.add(new NodePair(child, this));
        }
        return result;
    }

    public void eachSubPair(Consumer<NodePair> block) {
        block.accept(this);
        children().forEach((child) -> {
            child.eachSubPair(block);
        });
    }

    public List<NodePair> getSubPairs() {
        List<NodePair> result = new ArrayList<>();
        result.add(this);
        children().forEach((child) -> {
            result.addAll(child.getSubPairs());
        });
        return result;
    }
}
