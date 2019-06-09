package com.github.sider.java_see;

import com.github.sider.java_see.ast.AST;
import com.github.sider.java_see.libs.Tuple3;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public class Analyzer {
    public final Config config;
    public final String rule;
    public final List<Script> scripts;

    public void run(Consumer<Tuple3<Script, Rule, NodePair>> consumer) {
        for(Script script:this.scripts) {
            Set<Rule> rules = config.rulesForPath(script.path);
            script.rootPair().eachSubPair((nodePair) -> {
                for(Rule rule:rules) {
                    if(rule.patterns.stream().anyMatch((pattern) -> testPair(nodePair, pattern))) {
                        consumer.accept(new Tuple3<>(script, rule, nodePair));
                    }
                }
            });
        }
    }

    public boolean testPair(NodePair pair, AST.PatternNode pattern) {
        return pattern.matches(pair);
    }
}
