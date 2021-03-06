package com.github.sider.javasee;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.lib.Tuple3;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public class Analyzer {
    public final Config config;
    public final String rule;
    public final List<JavaFile> javaFiles;

    public void run(Consumer<Tuple3<JavaFile, Rule, NodePair>> consumer) {
        for(JavaFile javaFile: this.javaFiles) {
            javaFile.rootPair().eachSubPair((nodePair) -> {
                for (var rule: config.rules) {
                    if(rule.patterns.stream().anyMatch((pattern) -> testPair(nodePair, pattern))) {
                        consumer.accept(new Tuple3<>(javaFile, rule, nodePair));
                    }
                }
            });
        }
    }

    public void find(AST.Expression pattern, BiConsumer<JavaFile, NodePair> consumer) {
        for(var script: javaFiles) {
            script.rootPair().eachSubPair((nodePair) -> {
                if(testPair(nodePair, pattern)) {
                    consumer.accept(script, nodePair);
                }
            });
        }
    }

    public boolean testPair(NodePair pair, AST.PatternNode pattern) {
        return pattern.matches(pair);
    }
}
