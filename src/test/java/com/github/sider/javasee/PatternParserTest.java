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
        assertSame(e.getClass(), AST.BooleanLiteral.class);
        var et =(AST.BooleanLiteral)e;
        assertTrue(et.value);

        e = parser("false").WholeExpression();
        assertSame(e.getClass(), AST.BooleanLiteral.class);
        et = (AST.BooleanLiteral)e;
        assertFalse(et.value);

    }

    @Test
    public void testConditionalAndExpression() throws Exception {
        var e = parser("_ && _").WholeExpression();
        assertSame(e.getClass(), AST.ConditionalAndExpression.class);
        var et = (AST.ConditionalAndExpression)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testConditionalOrExpression() throws Exception {
        var e = parser("_ || _").WholeExpression();
        assertSame(e.getClass(), AST.ConditionalOrExpression.class);
        var et = (AST.ConditionalOrExpression)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testBitwiseAndExpression() throws Exception {
        var e = parser("_ & _").WholeExpression();
        assertSame(e.getClass(), AST.BitwiseAndExpression.class);
        var et = (AST.BitwiseAndExpression)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testBitwiseOrExpression() throws Exception {
        var e = parser("_ | _").WholeExpression();
        assertSame(e.getClass(), AST.BitwiseOrExpression.class);
        var et = (AST.BitwiseOrExpression)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testAdd() throws Exception {
        var e = parser("_ + _").WholeExpression();
        assertSame(e.getClass(), AST.Addition.class);
        var et = (AST.Addition)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testSubtract() throws Exception {
        var e = parser("_ - _").WholeExpression();
        assertSame(e.getClass(), AST.Subtraction.class);
        var et = (AST.Subtraction)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testMultiply() throws Exception {
        var e = parser("_ * _").WholeExpression();
        assertSame(e.getClass(), AST.Multiplication.class);
        var et = (AST.Multiplication)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testDivide() throws Exception {
        var e = parser("_ / _").WholeExpression();
        assertSame(e.getClass(), AST.Division.class);
        var et = (AST.Division)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testRemainder() throws Exception {
        var e = parser("_ %  _").WholeExpression();
        assertSame(e.getClass(), AST.Remainder.class);
        var et = (AST.Remainder)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testSimpleAssignment() throws Exception {
        var e = parser("_ =  _").WholeExpression();
        assertSame(e.getClass(), AST.SimpleAssignment.class);
        var et = (AST.SimpleAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testAdditionAssignment() throws Exception {
        var e = parser("_ +=  _").WholeExpression();
        assertSame(e.getClass(), AST.AdditionAssignment.class);
        var et = (AST.AdditionAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testSubtractionAssignment() throws Exception {
        var e = parser("_ -=  _").WholeExpression();
        assertSame(e.getClass(), AST.SubtractionAssignment.class);
        var et = (AST.SubtractionAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testMultiplicationAssignment() throws Exception {
        var e = parser("_ *=  _").WholeExpression();
        assertSame(e.getClass(), AST.MultiplicationAssignment.class);
        var et = (AST.MultiplicationAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testDivisionAssignment() throws Exception {
        var e = parser("_ /=  _").WholeExpression();
        assertSame(e.getClass(), AST.DivisionAssignment.class);
        var et = (AST.DivisionAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testRemainderAssignment() throws Exception {
        var e = parser("_ %=  _").WholeExpression();
        assertSame(e.getClass(), AST.RemainderAssignment.class);
        var et = (AST.RemainderAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testBitwiseAndAssignment() throws Exception {
        var e = parser("_ &=  _").WholeExpression();
        assertSame(e.getClass(), AST.BitwiseAndAssignment.class);
        var et = (AST.BitwiseAndAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testBitwiseOrAssignment() throws Exception {
        var e = parser("_ |=  _").WholeExpression();
        assertSame(e.getClass(), AST.BitwiseOrAssignment.class);
        var et = (AST.BitwiseOrAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testXorAssignment() throws Exception {
        var e = parser("_ ^=  _").WholeExpression();
        assertSame(e.getClass(), AST.XorAssignment.class);
        var et = (AST.XorAssignment)e;
        assertSame(et.lhs.getClass(), AST.Wildcard.class);
        assertSame(et.rhs.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testPositive() throws Exception {
        var e = parser("+ _").WholeExpression();
        assertSame(e.getClass(), AST.UnaryPlusExpression.class);
        var et = (AST.UnaryPlusExpression)e;
        assertSame(et.expression.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testNegative() throws Exception {
        var e = parser("-_").WholeExpression();
        assertSame(e.getClass(), AST.UnaryMinusExpression.class);
        var et = (AST.UnaryMinusExpression)e;
        assertSame(et.expression.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testLogicalComplement() throws Exception {
        var e = parser("!_").WholeExpression();
        assertSame(e.getClass(), AST.LogicalComplementExpression.class);
        var et = (AST.LogicalComplementExpression)e;
        assertSame(et.expression.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testBitwiseComplement() throws Exception {
        var e = parser("~_").WholeExpression();
        assertSame(e.getClass(), AST.BitwiseComplementExpression.class);
        var et = (AST.BitwiseComplementExpression)e;
        assertSame(et.expression.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testBooleanWildcard() throws Exception {
        var e = parser("@boolean").WholeExpression();
        assertSame(e.getClass(), AST.BooleanWildcard.class);
    }

    @Test
    public void testCharacterLiteral() throws Exception {
        var e = parser("'H'").WholeExpression();
        assertSame(e.getClass(), AST.CharacterLiteral.class);
        var et =(AST.CharacterLiteral)e;
        assertEquals("H", et.value);
    }

    @Test
    public void testStringLiteral() throws Exception {
        var e = parser("\"Hello\"").WholeExpression();
        assertSame(e.getClass(), AST.StringLiteral.class);
        var et =(AST.StringLiteral)e;
        assertEquals("Hello", et.value);
    }

    @Test
    public void testAnyID() throws Exception {
        var e = parser("@?").WholeExpression();
        assertSame(e.getClass(), AST.AnyID.class);
    }

    @Test
    public void testPrefixIncrementExpression() throws Exception {
        var e = parser("++x").WholeExpression();
        assertSame(e.getClass(), AST.PrefixIncrementExpression.class);
    }

    @Test
    public void testPrefixDecrementExpression() throws Exception {
        var e = parser("--x").WholeExpression();
        assertSame(e.getClass(), AST.PrefixDecrementExpression.class);
    }

    @Test
    public void testPostfixIncrementExpression() throws Exception {
        var e = parser("x++").WholeExpression();
        assertSame(e.getClass(), AST.PostIncrement.class);
    }

    @Test
    public void testPostfixDecrementExpression() throws Exception {
        var e = parser("x--").WholeExpression();
        assertSame(e.getClass(), AST.PostDecrement.class);
    }

    @Test
    public void testInstanceCreation() throws Exception {
        AST.Expression e;
        e = parser("new Object()").WholeExpression();
        assertSame(e.getClass(), AST.InstanceCreationExpression.class);
        assertEquals("Object", ((AST.InstanceCreationExpression)e).name);
        assertEquals(0, ((AST.InstanceCreationExpression)e).parameters.size());

        e = parser("new Object(1)").WholeExpression();
        assertSame(e.getClass(), AST.InstanceCreationExpression.class);
        assertEquals("Object", ((AST.InstanceCreationExpression)e).name);
        assertEquals(1, ((AST.InstanceCreationExpression)e).parameters.size());
        assertSame(((AST.InstanceCreationExpression)e).parameters.get(0).getClass(), AST.IntLiteral.class);
    }

    @Test
    public void testArrayCreation() throws Exception {
        AST.Expression e;
        e = parser("new Object#[10]").WholeExpression();
        assertSame(e.getClass(), AST.ArrayCreationExpression.class);
        assertEquals("Object", ((AST.ArrayCreationExpression)e).name);
        assertEquals(1, ((AST.ArrayCreationExpression)e).levels.size());
        assertEquals(10, ((AST.IntLiteral)((AST.ArrayCreationExpression)e).levels.get(0)).value);

        e = parser("new Object#[20]#[30]").WholeExpression();
        assertSame(e.getClass(), AST.ArrayCreationExpression.class);
        assertEquals("Object", ((AST.ArrayCreationExpression)e).name);
        assertEquals(2, ((AST.ArrayCreationExpression)e).levels.size());
        assertEquals(20, ((AST.IntLiteral)((AST.ArrayCreationExpression)e).levels.get(0)).value);
        assertEquals(30, ((AST.IntLiteral)((AST.ArrayCreationExpression)e).levels.get(1)).value);
    }

    @Test
    public void testStringWildcard() throws Exception {
        var e = parser("@String").WholeExpression();
        assertSame(e.getClass(), AST.StringWildcard.class);
    }

    @Test
    public void testDoubleLiteral() throws Exception {
        var e = parser("1.5").WholeExpression();
        assertSame(e.getClass(), AST.DoubleLiteral.class);
        var et =(AST.DoubleLiteral)e;
        assertEquals(1.5, et.value);
    }

    @Test
    public void testDoubleWildcard() throws Exception {
        var e = parser("@double").WholeExpression();
        assertSame(e.getClass(), AST.DoubleWildcard.class);
    }

    @Test
    public void testConditionalExpression() throws Exception {
        AST.Expression e;
        AST.ConditionalExpression et;

        e = parser("_ ? 1 : true").WholeExpression();
        assertSame(e.getClass(), AST.ConditionalExpression.class);
        et = (AST.ConditionalExpression)e;
        assertSame(et.condition.getClass(), AST.Wildcard.class);
        assertSame(et.thenPart.getClass(), AST.IntLiteral.class);
        assertSame(et.elsepart.getClass(), AST.BooleanLiteral.class);


        AST.RelationalExpression er;
        e = parser("2 < 3 ? \"Foo\" : \"Bar\"").WholeExpression();
        assertSame(e.getClass(), AST.ConditionalExpression.class);
        et = (AST.ConditionalExpression)e;
        assertSame(et.condition.getClass(), AST.LessThan.class);
        er = (AST.RelationalExpression) et.condition;
        assertEquals("<", er.symbol);
        assertSame(er.lhs.getClass(), AST.IntLiteral.class);
        assertSame(er.rhs.getClass(), AST.IntLiteral.class);
        assertSame(et.thenPart.getClass(), AST.StringLiteral.class);
        assertSame(et.elsepart.getClass(), AST.StringLiteral.class);
    }

    @Test
    public void testMoreComplexConditionalExpression() throws Exception {
        AST.Expression e;
        AST.ConditionalExpression et;
        AST.RelationalExpression er;

        e = parser("2 < 3 ? \"Foo\" : \"Bar\"").WholeExpression();
        assertSame(e.getClass(), AST.ConditionalExpression.class);
        et = (AST.ConditionalExpression)e;
        assertSame(et.condition.getClass(), AST.LessThan.class);
        er = (AST.RelationalExpression) et.condition;
        assertEquals("<", er.symbol);
        assertSame(er.lhs.getClass(), AST.IntLiteral.class);
        assertSame(er.rhs.getClass(), AST.IntLiteral.class);
        assertSame(et.thenPart.getClass(), AST.StringLiteral.class);
        assertSame(et.elsepart.getClass(), AST.StringLiteral.class);
    }

    @Test
    public void testInstanceof() throws Exception {
        AST.Expression e;
        AST.InstanceofExpression et;

        e = parser("_ instanceof String").WholeExpression();
        assertSame(e.getClass(), AST.InstanceofExpression.class);
        et = (AST.InstanceofExpression)e;
        assertSame(et.target.getClass(), AST.Wildcard.class);
        assertEquals("String", et.type.getName());

        e = parser("_ instanceof _").WholeExpression();
        assertSame(e.getClass(), AST.InstanceofExpression.class);
        et = (AST.InstanceofExpression)e;
        assertSame(et.target.getClass(), AST.Wildcard.class);
        assertSame(et.type.getClass(), AST.PlaceholderTypeNode.class);
    }

    @Test
    public void testIntLiteral() throws Exception {
        var e = parser("2").WholeExpression();
        assertSame(e.getClass(), AST.IntLiteral.class);
        var et =(AST.IntLiteral)e;
        assertEquals(2, et.value);
    }

    @Test
    public void testClassLiteral() throws Exception {
        var e = parser("class [java.lang.Object]").WholeExpression();
        assertSame(e.getClass(), AST.ClassLiteral.class);
        var et =(AST.ClassLiteral)e;
        assertEquals("Object", et.simpleName);
        assertEquals(List.of("java", "lang"), et.packageFragments);
    }

    @Test
    public void testIntWildcard() throws Exception {
        var e = parser("@int").WholeExpression();
        assertSame(e.getClass(), AST.IntWildcard.class);
    }

    @Test
    public void testNullLiteral() throws Exception {
        var e = parser("null").WholeExpression();
        assertSame(e.getClass(), AST.NullLiteral.class);
    }

    @Test
    public void testThisLiteral() throws Exception {
        var e = parser("this").WholeExpression();
        assertSame(e.getClass(), AST.ThisLiteral.class);
    }

    @Test
    public void testWildcard() throws Exception {
        var e = parser("_").WholeExpression();
        assertSame(e.getClass(), AST.Wildcard.class);
    }

    @Test
    public void testThisMethodCall() throws Exception {
        var e = parser("this.apply(_)").WholeExpression();
        assertSame(e.getClass(), AST.MethodCall.class);
        var et = (AST.MethodCall)e;

        assertSame(et.receiver.getClass(), AST.ThisLiteral.class);
        assertEquals("apply", et.name);
        assertEquals(1, et.parameters.size());
        assertSame(et.parameters.get(0).getClass(), AST.Wildcard.class);
    }

    @Test
    public void testWildcardMethodCall() throws Exception {
        var e = parser("_.apply(this, _)").WholeExpression();
        assertSame(e.getClass(), AST.MethodCall.class);
        var et = (AST.MethodCall)e;

        assertSame(et.receiver.getClass(), AST.Wildcard.class);
        assertEquals("apply", et.name);
        assertEquals(2, et.parameters.size());
        assertSame(et.parameters.get(0).getClass(), AST.ThisLiteral.class);
        assertSame(et.parameters.get(1).getClass(), AST.Wildcard.class);
    }

    @Test
    public void testSingleLineComment() throws Exception {
        var e = parser("1 + 2 // line comment").WholeExpression();
        assertSame(e.getClass(), AST.Addition.class);
        var et = (AST.Addition)e;

        assertSame(et.lhs.getClass(), AST.IntLiteral.class);
        assertEquals(1, ((AST.IntLiteral)et.lhs).value);
        assertSame(et.rhs.getClass(), AST.IntLiteral.class);
        assertEquals(2, ((AST.IntLiteral)et.rhs).value);
    }

    @Test
    public void testMultiLineComment() throws Exception {
        var e = parser("/* comment1 */ 1 + 2 /* comment2 */").WholeExpression();
        assertSame(e.getClass(), AST.Addition.class);
        var et = (AST.Addition)e;

        assertSame(et.lhs.getClass(), AST.IntLiteral.class);
        assertEquals(1, ((AST.IntLiteral)et.lhs).value);
        assertSame(et.rhs.getClass(), AST.IntLiteral.class);
        assertEquals(2, ((AST.IntLiteral)et.rhs).value);
    }

    @Test
    public void testLambda() throws Exception {
        var parsed = parser("_.map(->)").WholeExpression();
        assertSame(parsed.getClass(), AST.MethodCall.class);
        var methodCall = (AST.MethodCall)parsed;
        assertSame(methodCall.receiver.getClass(), AST.Wildcard.class);
        assertEquals(1, methodCall.parameters.size());
        assertSame(methodCall.parameters.get(0).getClass(), AST.LambdaPattern.class);
    }
}
