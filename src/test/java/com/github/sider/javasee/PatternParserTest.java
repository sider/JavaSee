package com.github.sider.javasee;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.parser.JavaSeeParser;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class PatternParserTest {
    private JavaSeeParser parser(String input) {
        return new JavaSeeParser(new StringReader(input));
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
    public void testAdd() throws Exception {
        var e = parser("_ + _").Expression();
        assertTrue(e instanceof AST.Addition);
        var et = (AST.Addition)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testSubtract() throws Exception {
        var e = parser("_ - _").Expression();
        assertTrue(e instanceof AST.Subtraction);
        var et = (AST.Subtraction)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testMultiply() throws Exception {
        var e = parser("_ * _").Expression();
        assertTrue(e instanceof AST.Multiplication);
        var et = (AST.Multiplication)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testDivide() throws Exception {
        var e = parser("_ / _").Expression();
        assertTrue(e instanceof AST.Division);
        var et = (AST.Division)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testModulo() throws Exception {
        var e = parser("_ %  _").Expression();
        assertTrue(e instanceof AST.Remainder);
        var et = (AST.Remainder)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testPositive() throws Exception {
        var e = parser("+ _").Expression();
        assertTrue(e instanceof AST.UnaryPlusExpression);
        var et = (AST.UnaryPlusExpression)e;
        assertTrue(et.expression instanceof AST.Wildcard);
    }

    @Test
    public void testNegative() throws Exception {
        var e = parser("-_").Expression();
        assertTrue(e instanceof AST.UnaryMinusExpression);
        var et = (AST.UnaryMinusExpression)e;
        assertTrue(et.expression instanceof AST.Wildcard);
    }

    @Test
    public void testNot() throws Exception {
        var e = parser("!_").Expression();
        assertTrue(e instanceof AST.LogicalComplementExpression);
        var et = (AST.LogicalComplementExpression)e;
        assertTrue(et.expression instanceof AST.Wildcard);
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
