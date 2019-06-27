package com.github.sider.javasee;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.parser.JavaSeeParser;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

public class PatternParserTest {
    private JavaSeeParser parser(String input) {
        return new JavaSeeParser(new StringReader(input));
    }

    @Test
    public void testBooleanLiteral() throws Exception {
        var e = parser("true").WholeExpression();
        assertTrue(e instanceof AST.BooleanLiteral);
        var et =(AST.BooleanLiteral)e;
        assertTrue(et.value);

        e = parser("false").WholeExpression();
        assertTrue(e instanceof AST.BooleanLiteral);
        et = (AST.BooleanLiteral)e;
        assertFalse(et.value);

    }

    @Test
    public void testConditionalAndExpression() throws Exception {
        var e = parser("_ && _").WholeExpression();
        assertTrue(e instanceof AST.ConditionalAndExpression);
        var et = (AST.ConditionalAndExpression)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testConditionalOrExpression() throws Exception {
        var e = parser("_ || _").WholeExpression();
        assertTrue(e instanceof AST.ConditionalOrExpression);
        var et = (AST.ConditionalOrExpression)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testBitwiseAndExpression() throws Exception {
        var e = parser("_ & _").WholeExpression();
        assertTrue(e instanceof AST.BitwiseAndExpression);
        var et = (AST.BitwiseAndExpression)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testBitwiseOrExpression() throws Exception {
        var e = parser("_ | _").WholeExpression();
        assertTrue(e instanceof AST.BitwiseOrExpression);
        var et = (AST.BitwiseOrExpression)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testAdd() throws Exception {
        var e = parser("_ + _").WholeExpression();
        assertTrue(e instanceof AST.Addition);
        var et = (AST.Addition)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testSubtract() throws Exception {
        var e = parser("_ - _").WholeExpression();
        assertTrue(e instanceof AST.Subtraction);
        var et = (AST.Subtraction)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testMultiply() throws Exception {
        var e = parser("_ * _").WholeExpression();
        assertTrue(e instanceof AST.Multiplication);
        var et = (AST.Multiplication)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testDivide() throws Exception {
        var e = parser("_ / _").WholeExpression();
        assertTrue(e instanceof AST.Division);
        var et = (AST.Division)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testRemainder() throws Exception {
        var e = parser("_ %  _").WholeExpression();
        assertTrue(e instanceof AST.Remainder);
        var et = (AST.Remainder)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testSimpleAssignment() throws Exception {
        var e = parser("_ =  _").WholeExpression();
        assertTrue(e instanceof AST.SimpleAssignment);
        var et = (AST.SimpleAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testAdditionAssignment() throws Exception {
        var e = parser("_ +=  _").WholeExpression();
        assertTrue(e instanceof AST.AdditionAssignment);
        var et = (AST.AdditionAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testSubtractionAssignment() throws Exception {
        var e = parser("_ -=  _").WholeExpression();
        assertTrue(e instanceof AST.SubtractionAssignment);
        var et = (AST.SubtractionAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testMultiplicationAssignment() throws Exception {
        var e = parser("_ *=  _").WholeExpression();
        assertTrue(e instanceof AST.MultiplicationAssignment);
        var et = (AST.MultiplicationAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testDivisionAssignment() throws Exception {
        var e = parser("_ /=  _").WholeExpression();
        assertTrue(e instanceof AST.DivisionAssignment);
        var et = (AST.DivisionAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testRemainderAssignment() throws Exception {
        var e = parser("_ %=  _").WholeExpression();
        assertTrue(e instanceof AST.RemainderAssignment);
        var et = (AST.RemainderAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testBitwiseAndAssignment() throws Exception {
        var e = parser("_ &=  _").WholeExpression();
        assertTrue(e instanceof AST.BitwiseAndAssignment);
        var et = (AST.BitwiseAndAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testBitwiseOrAssignment() throws Exception {
        var e = parser("_ |=  _").WholeExpression();
        assertTrue(e instanceof AST.BitwiseOrAssignment);
        var et = (AST.BitwiseOrAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testXorAssignment() throws Exception {
        var e = parser("_ ^=  _").WholeExpression();
        assertTrue(e instanceof AST.XorAssignment);
        var et = (AST.XorAssignment)e;
        assertTrue(et.lhs instanceof AST.Wildcard);
        assertTrue(et.rhs instanceof AST.Wildcard);
    }

    @Test
    public void testPositive() throws Exception {
        var e = parser("+ _").WholeExpression();
        assertTrue(e instanceof AST.UnaryPlusExpression);
        var et = (AST.UnaryPlusExpression)e;
        assertTrue(et.expression instanceof AST.Wildcard);
    }

    @Test
    public void testNegative() throws Exception {
        var e = parser("-_").WholeExpression();
        assertTrue(e instanceof AST.UnaryMinusExpression);
        var et = (AST.UnaryMinusExpression)e;
        assertTrue(et.expression instanceof AST.Wildcard);
    }

    @Test
    public void testLogicalComplement() throws Exception {
        var e = parser("!_").WholeExpression();
        assertTrue(e instanceof AST.LogicalComplementExpression);
        var et = (AST.LogicalComplementExpression)e;
        assertTrue(et.expression instanceof AST.Wildcard);
    }

    @Test
    public void testBitwiseComplement() throws Exception {
        var e = parser("~_").WholeExpression();
        assertTrue(e instanceof AST.BitwiseComplementExpression);
        var et = (AST.BitwiseComplementExpression)e;
        assertTrue(et.expression instanceof AST.Wildcard);
    }

    @Test
    public void testBooleanWildcard() throws Exception {
        var e = parser("@boolean").WholeExpression();
        assertTrue(e instanceof AST.BooleanWildcard);
    }

    @Test
    public void testCharacterLiteral() throws Exception {
        var e = parser("'H'").WholeExpression();
        assertTrue(e instanceof AST.CharacterLiteral);
        var et =(AST.CharacterLiteral)e;
        assertEquals("H", et.value);
    }

    @Test
    public void testStringLiteral() throws Exception {
        var e = parser("\"Hello\"").WholeExpression();
        assertTrue(e instanceof AST.StringLiteral);
        var et =(AST.StringLiteral)e;
        assertEquals("Hello", et.value);
    }

    @Test
    public void testAnyID() throws Exception {
        var e = parser("@?").WholeExpression();
        assertTrue(e instanceof AST.AnyID);
    }

    @Test
    public void testPrefixIncrementExpression() throws Exception {
        var e = parser("++x").WholeExpression();
        assertTrue(e instanceof AST.PrefixIncrementExpression);
    }

    @Test
    public void testPrefixDecrementExpression() throws Exception {
        var e = parser("--x").WholeExpression();
        assertTrue(e instanceof AST.PrefixDecrementExpression);
    }

    @Test
    public void testPostfixIncrementExpression() throws Exception {
        var e = parser("x++").WholeExpression();
        assertTrue(e instanceof AST.PostIncrement);
    }

    @Test
    public void testPostfixDecrementExpression() throws Exception {
        var e = parser("x--").WholeExpression();
        assertTrue(e instanceof AST.PostDecrement);
    }

    @Test
    public void testInstanceCreation() throws Exception {
        AST.Expression e;
        e = parser("new Object()").WholeExpression();
        assertTrue(e instanceof AST.InstanceCreationExpression);
        assertEquals("Object", ((AST.InstanceCreationExpression)e).name);
        assertEquals(0, ((AST.InstanceCreationExpression)e).parameters.size());

        e = parser("new Object(1)").WholeExpression();
        assertTrue(e instanceof AST.InstanceCreationExpression);
        assertEquals("Object", ((AST.InstanceCreationExpression)e).name);
        assertEquals(1, ((AST.InstanceCreationExpression)e).parameters.size());
        assertTrue(((AST.InstanceCreationExpression)e).parameters.get(0) instanceof AST.IntLiteral);
    }

    @Test
    public void testArrayCreation() throws Exception {
        AST.Expression e;
        e = parser("new Object#[10]").WholeExpression();
        assertTrue(e instanceof AST.ArrayCreationExpression);
        assertEquals("Object", ((AST.ArrayCreationExpression)e).name);
        assertEquals(1, ((AST.ArrayCreationExpression)e).levels.size());
        assertEquals(10, ((AST.IntLiteral)((AST.ArrayCreationExpression)e).levels.get(0)).value);

        e = parser("new Object#[20]#[30]").WholeExpression();
        assertTrue(e instanceof AST.ArrayCreationExpression);
        assertEquals("Object", ((AST.ArrayCreationExpression)e).name);
        assertEquals(2, ((AST.ArrayCreationExpression)e).levels.size());
        assertEquals(20, ((AST.IntLiteral)((AST.ArrayCreationExpression)e).levels.get(0)).value);
        assertEquals(30, ((AST.IntLiteral)((AST.ArrayCreationExpression)e).levels.get(1)).value);
    }

    @Test
    public void testStringWildcard() throws Exception {
        var e = parser("@String").WholeExpression();
        assertTrue(e instanceof AST.StringWildcard);
    }

    @Test
    public void testDoubleLiteral() throws Exception {
        var e = parser("1.5").WholeExpression();
        assertTrue(e instanceof AST.DoubleLiteral);
        var et =(AST.DoubleLiteral)e;
        assertEquals(1.5, et.value);
    }

    @Test
    public void testDoubleWildcard() throws Exception {
        var e = parser("@double").WholeExpression();
        assertTrue(e instanceof AST.DoubleWildcard);
    }

    @Test
    public void testConditionalExpression() throws Exception {
        var e = parser("_ ? 1 : true").WholeExpression();
        assertTrue(e instanceof AST.ConditionalExpression);
        var et = (AST.ConditionalExpression)e;
        assertTrue(et.condition instanceof AST.Wildcard);
        assertTrue(et.thenPart instanceof AST.IntLiteral);
        assertTrue(et.elsepart instanceof AST.BooleanLiteral);
    }

    @Test
    public void testInstanceof() throws Exception {
        AST.Expression e;
        AST.InstanceofExpression et;

        e = parser("_ instanceof String").WholeExpression();
        assertTrue(e instanceof AST.InstanceofExpression);
        et = (AST.InstanceofExpression)e;
        assertTrue(et.target instanceof AST.Wildcard);
        assertEquals("String", et.type.getName());

        e = parser("_ instanceof _").WholeExpression();
        assertTrue(e instanceof AST.InstanceofExpression);
        et = (AST.InstanceofExpression)e;
        assertTrue(et.target instanceof AST.Wildcard);
        assertTrue(et.type instanceof AST.PlaceholderTypeNode);
    }

    @Test
    public void testIntLiteral() throws Exception {
        var e = parser("2").WholeExpression();
        assertTrue(e instanceof AST.IntLiteral);
        var et =(AST.IntLiteral)e;
        assertEquals(2, et.value);
    }

    @Test
    public void testClassLiteral() throws Exception {
        var e = parser("class [java.lang.Object]").WholeExpression();
        assertTrue(e instanceof AST.ClassLiteral);
        var et =(AST.ClassLiteral)e;
        assertEquals("Object", et.simpleName);
        assertEquals(List.of("java", "lang"), et.packageFragments);
    }

    @Test
    public void testIntWildcard() throws Exception {
        var e = parser("@int").WholeExpression();
        assertTrue(e instanceof AST.IntWildcard);
    }

    @Test
    public void testNullLiteral() throws Exception {
        var e = parser("null").WholeExpression();
        assertTrue(e instanceof AST.NullLiteral);
    }

    @Test
    public void testThisLiteral() throws Exception {
        var e = parser("this").WholeExpression();
        assertTrue(e instanceof AST.ThisLiteral);
    }

    @Test
    public void testWildcard() throws Exception {
        var e = parser("_").WholeExpression();
        assertTrue(e instanceof AST.Wildcard);
    }

    @Test
    public void testThisMethodCall() throws Exception {
        var e = parser("this.apply(_)").WholeExpression();
        assertTrue(e instanceof AST.MethodCall);
        var et = (AST.MethodCall)e;

        assertTrue(et.receiver instanceof AST.ThisLiteral);
        assertEquals("apply", et.name);
        assertEquals(1, et.parameters.size());
        assertTrue(et.parameters.get(0) instanceof AST.Wildcard);
    }

    @Test
    public void testWildcardMethodCall() throws Exception {
        var e = parser("_.apply(this, _)").WholeExpression();
        assertTrue(e instanceof AST.MethodCall);
        var et = (AST.MethodCall)e;

        assertTrue(et.receiver instanceof AST.Wildcard);
        assertEquals("apply", et.name);
        assertEquals(2, et.parameters.size());
        assertTrue(et.parameters.get(0) instanceof AST.ThisLiteral);
        assertTrue(et.parameters.get(1) instanceof AST.Wildcard);
    }

    @Test
    public void testSingleLineComment() throws Exception {
        var e = parser("1 + 2 // line comment").WholeExpression();
        assertTrue(e instanceof AST.Addition);
        var et = (AST.Addition)e;

        assertTrue(et.lhs instanceof AST.IntLiteral);
        assertEquals(1, ((AST.IntLiteral)et.lhs).value);
        assertTrue(et.rhs instanceof AST.IntLiteral);
        assertEquals(2, ((AST.IntLiteral)et.rhs).value);
    }

    @Test
    public void testMultiLineComment() throws Exception {
        var e = parser("/* comment1 */ 1 + 2 /* comment2 */").WholeExpression();
        assertTrue(e instanceof AST.Addition);
        var et = (AST.Addition)e;

        assertTrue(et.lhs instanceof AST.IntLiteral);
        assertEquals(1, ((AST.IntLiteral)et.lhs).value);
        assertTrue(et.rhs instanceof AST.IntLiteral);
        assertEquals(2, ((AST.IntLiteral)et.rhs).value);
    }


}
