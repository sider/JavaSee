package com.github.kmizu.jquerly;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class JavaParser {
    public JavaParser() {
    }

    public CompilationUnit parse(String code) {
        return StaticJavaParser.parse(code);
    }

    public static void main(String[] args) {
        var x = new JavaParser().parse("public class A{}");
        System.out.println(x.getType(0).getName());
    }
}
