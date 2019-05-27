package com.github.kmizu.java_see;

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.kmizu.java_see.parser.JavaSeeParser;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class JavaParserTest {
    private JavaParser parser() {
        return new JavaParser();
    }

    private JavaSeeParser parser(String input) {
        return new JavaSeeParser(new StringReader(input));
    }

    @Test
    public void testJavaParser() {
        var tree = parser().parse("public class A{}");
        assertEquals("A", tree.getType(0).getName().asString());
    }

    @Test
    public void testEachSubPair1() throws Exception {
        var tree = parser().parse("public class A{ public static void main(String[] args) { System.out.println(1); Math.abs(1); } }");
        var pattern = parser("_.abs(1)").Expression();
        new NodePair(tree, null).eachSubPair((pair) -> {
            if(pattern.matches(pair)) {
                var node = pair.node;
                assertTrue(node instanceof MethodCallExpr);
                var call = (MethodCallExpr)node;
                assertEquals("abs", call.getName().asString());
                assertTrue(call.getArguments().get(0) instanceof IntegerLiteralExpr);
                var arg = (IntegerLiteralExpr)call.getArguments().get(0);
                assertEquals(1, arg.asInt());
            }
        });
    }

    @Test
    public void testEachSubPair2() throws Exception {
        var tree = parser().parse("public class A{ public static void main(String[] args) { new Hello(1); } }");
        var pattern = parser("new Hello(_)").Expression();
        new NodePair(tree, null).eachSubPair((pair) -> {
            if(pattern.matches(pair)) {
                var node = pair.node;
                assertTrue(node instanceof ObjectCreationExpr);
                var newObject = (ObjectCreationExpr)node;
                assertEquals("Hello", newObject.getType().asString());
                assertTrue(newObject.getArguments().get(0) instanceof IntegerLiteralExpr);
                var arg = (IntegerLiteralExpr)newObject.getArguments().get(0);
                assertEquals(1, arg.asInt());
            }
        });
    }

}
