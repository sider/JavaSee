package com.github.sider.javasee;

import com.github.sider.javasee.ast.AST;
import com.github.sider.javasee.parser.JavaSeeParser;
import jdk.jfr.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionPrecedenceTest {
    private JavaSeeParser parser(String input) {
        return new JavaSeeParser(new StringReader(input));
    }

    @Test
    @DisplayName("Check +'s precedence  == -'s precedence")
    public void testAdditionAndSubtraction() throws Exception {
        AST.Expression e1;
        AST.BinaryExpression e2;
        e1 = parser("1 + 2 - 3").WholeExpression();
        assertTrue(e1 instanceof AST.Subtraction);
        e2 = (AST.BinaryExpression) e1;
        assertTrue(e2.lhs instanceof AST.Addition);
        assertTrue(e2.rhs instanceof AST.IntLiteral);

        e2 = (AST.BinaryExpression) e2.lhs;
        assertTrue(e2.lhs instanceof AST.IntLiteral);
        assertTrue(e2.rhs instanceof AST.IntLiteral);
    }

    @Test
    @DisplayName("Check *'s precedence  == /'s precedence")
    public void testMultiplicationAndDivision() throws Exception {
        AST.Expression e1;
        AST.BinaryExpression e2;
        e1 = parser("1 * 2 / 3").WholeExpression();
        assertTrue(e1 instanceof AST.Division);
        e2 = (AST.BinaryExpression) e1;
        assertTrue(e2.lhs instanceof AST.Multiplication);
        assertTrue(e2.rhs instanceof AST.IntLiteral);

        e2 = (AST.BinaryExpression) e2.lhs;
        assertTrue(e2.lhs instanceof AST.IntLiteral);
        assertTrue(e2.rhs instanceof AST.IntLiteral);
    }

    @Test
    @DisplayName("Check * has higer precedence than +")
    public void testAdditionAndMultiplication() throws Exception {
        AST.Expression e1;
        AST.BinaryExpression e2;
        e1 = parser("1 + 2 * 3").WholeExpression();
        assertTrue(e1 instanceof AST.Addition);
        e2 = (AST.BinaryExpression) e1;
        assertTrue(e2.lhs instanceof AST.IntLiteral);
        assertTrue(e2.rhs instanceof AST.Multiplication);

        e2 = (AST.BinaryExpression) e2.rhs;
        assertTrue(e2.lhs instanceof AST.IntLiteral);
        assertTrue(e2.rhs instanceof AST.IntLiteral);
    }

    @Test
    @DisplayName("Check / has higer precedence than +")
    public void testAdditionAndDivision() throws Exception {
        AST.Expression e1;
        AST.BinaryExpression e2;
        e1 = parser("1 + 2 / 3").WholeExpression();
        assertTrue(e1 instanceof AST.Addition);
        e2 = (AST.BinaryExpression) e1;
        assertTrue(e2.lhs instanceof AST.IntLiteral);
        assertTrue(e2.rhs instanceof AST.Division);

        e2 = (AST.BinaryExpression) e2.rhs;
        assertTrue(e2.lhs instanceof AST.IntLiteral);
        assertTrue(e2.rhs instanceof AST.IntLiteral);
    }


    @Test
    @DisplayName("Check % has higer precedence than +")
    public void testAdditionRemainder() throws Exception {
        AST.Expression e1;
        AST.BinaryExpression e2;
        e1 = parser("1 + 2 % 3").WholeExpression();
        assertTrue(e1 instanceof AST.Addition);
        e2 = (AST.BinaryExpression) e1;
        assertTrue(e2.lhs instanceof AST.IntLiteral);
        assertTrue(e2.rhs instanceof AST.Remainder);

        e2 = (AST.BinaryExpression) e2.rhs;
        assertTrue(e2.lhs instanceof AST.IntLiteral);
        assertTrue(e2.rhs instanceof AST.IntLiteral);
    }

}
