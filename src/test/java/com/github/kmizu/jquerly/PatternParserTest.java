package com.github.kmizu.jquerly;

import static org.junit.jupiter.api.Assertions.*;

import com.github.kmizu.jquerly.parser.JQuerlyParser;
import org.junit.jupiter.api.Test;

import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class PatternParserTest {
    private JQuerlyParser parser(String input) {
        return new JQuerlyParser(new StringReader(input));
    }

    @Test
    public void testBooleanLiteral() throws Exception {
        var e = parser("true").Expression();
        assertTrue(e instanceof AST.BooleanLiteral);
        var et =(AST.BooleanLiteral)e;
        assertTrue(et.value);

        e = parser("false").Expression();
        assertTrue(e instanceof AST.BooleanLiteral);
        et = (AST.BooleanLiteral)e;
        assertFalse(et.value);

    }

    @Test
    public void testBooleanWildcard() throws Exception {
        var e = parser(":boolean:").Expression();
        assertTrue(e instanceof AST.BooleanWildcard);
    }

    @Test
    public void testStringLiteral() throws Exception {
        var e = parser("\"Hello\"").Expression();
        assertTrue(e instanceof AST.StringLiteral);
        var et =(AST.StringLiteral)e;
        assertEquals("Hello", et.value);
    }

    @Test
    public void testStringWildcard() throws Exception {
        var e = parser(":String:").Expression();
        assertTrue(e instanceof AST.StringWildcard);
    }

    @Test
    public void testDoubleLiteral() throws Exception {
        var e = parser("1.5").Expression();
        assertTrue(e instanceof AST.DoubleLiteral);
        var et =(AST.DoubleLiteral)e;
        assertEquals(1.5, et.value);
    }

    @Test
    public void testDoubleWildcard() throws Exception {
        var e = parser(":double:").Expression();
        assertTrue(e instanceof AST.DoubleWildcard);
    }

    @Test
    public void testIntLiteral() throws Exception {
        var e = parser("2").Expression();
        assertTrue(e instanceof AST.IntLiteral);
        var et =(AST.IntLiteral)e;
        assertEquals(2, et.value);
    }

    @Test
    public void testIntWildcard() throws Exception {
        var e = parser(":int:").Expression();
        assertTrue(e instanceof AST.IntWildcard);
    }

    @Test
    public void testNullLiteral() throws Exception {
        var e = parser("null").Expression();
        assertTrue(e instanceof AST.NullLiteral);
    }

    @Test
    public void testThisLiteral() throws Exception {
        var e = parser("this").Expression();
        assertTrue(e instanceof AST.ThisLiteral);
    }

    @Test
    public void testWildcard() throws Exception {
        var e = parser("_").Expression();
        assertTrue(e instanceof AST.Wildcard);
    }

    @Test
    public void testThisMethodCall() throws Exception {
        var e = parser("this.apply(_)").Expression();
        assertTrue(e instanceof AST.MethodCall);
        var et = (AST.MethodCall)e;

        assertTrue(et.receiver instanceof AST.ThisLiteral);
        assertEquals("apply", et.name);
        assertEquals(1, et.parameters.size());
        assertTrue(et.parameters.get(0) instanceof AST.Wildcard);
    }

    @Test
    public void testWildcardMethodCall() throws Exception {
        var e = parser("_.apply(this, _)").Expression();
        assertTrue(e instanceof AST.MethodCall);
        var et = (AST.MethodCall)e;

        assertTrue(et.receiver instanceof AST.Wildcard);
        assertEquals("apply", et.name);
        assertEquals(2, et.parameters.size());
        assertTrue(et.parameters.get(0) instanceof AST.ThisLiteral);
        assertTrue(et.parameters.get(1) instanceof AST.Wildcard);
    }

}
