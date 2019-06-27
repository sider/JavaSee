package com.github.sider.javasee;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.parser.JavaSeeParser;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnicodePatternParserTest {
    private JavaSeeParser parser(String input) {
        return new JavaSeeParser(new StringReader(input));
    }

    @Test
    public void testUnicodeIdentifier() throws Exception {
        var e = parser("あ").WholeExpression();
        assertTrue(e instanceof AST.ID);
        var et = (AST.ID) e;
        assertEquals("あ", et.name);
    }

    @Test
    public void testUnicodeStringLiteral() throws Exception {
        var e = parser("\"あいうえお\"").WholeExpression();
        assertTrue(e instanceof AST.StringLiteral);
        var et = (AST.StringLiteral) e;
        assertEquals("あいうえお", et.value);
    }

    @Test
    public void testUnicodeEscapeInStringLiteral() throws Exception {
        //\u0041 is A
        var e = parser("\"\\u0041\"").WholeExpression();
        assertTrue(e instanceof AST.StringLiteral);
        var et = (AST.StringLiteral) e;
        assertEquals("A", et.value);
    }

    @Test
    public void testUnicodeEscapeInCharacterLiteral() throws Exception {
        //\u0042 is B
        var e = parser("\'\\u0042\'").WholeExpression();
        assertTrue(e instanceof AST.CharacterLiteral);
        var et = (AST.CharacterLiteral) e;
        assertEquals("B", et.value);
    }

}

