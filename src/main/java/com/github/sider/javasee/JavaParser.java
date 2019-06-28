package com.github.sider.javasee;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;

public class JavaParser {
    public JavaParser() {
    }

    public CompilationUnit parse(String code) {
        return StaticJavaParser.parse(code);
    }

    public Expression parseExpression(String expression) {
        return StaticJavaParser.parseExpression(expression);
    }

    public Statement parseStatement(String statement) {
        return StaticJavaParser.parseStatement(statement);
    }

    public Statement parseStatements(String statements) { return StaticJavaParser.parseStatement("{" + statements + "}"); }

    public static void main(String[] args) {
        var x = new JavaParser().parse("public class A{}");
        System.out.println(x.getType(0).getName());
    }
}
