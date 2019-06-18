package com.github.sider.javasee.ast;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.sider.javasee.NodePair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

public class AST {
    public static abstract class PatternNode {
        public abstract Location getLocation();
        public boolean matches(NodePair pair) {
            return testNode(pair.node);
        }
        public boolean testNode(Node node) {
            return false;
        }
    }

    public static class Kind {
        public final Expression expression;
        public Kind(Expression expression) {
            this.expression = expression;
        }
        public static class Any extends Kind {
            public Any(Expression expression) {
                super(expression);
            }
        }
        public static class Conditional extends Kind {
            public final boolean negated;
            public Conditional(Expression expression, boolean negated) {
                super(expression);
                this.negated = negated;
            }
        }
        public static class Discarded extends Kind {
            public final boolean negated;
            public Discarded(Expression expression, boolean negated) {
                super(expression);
                this.negated = negated;
            }
        }
    }

    public static abstract class Expression extends PatternNode {
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class RepeatedParameter extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return false;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ID extends Expression {
        public final Location location;
        public final String name;

        @Override
        public boolean testNode(Node node) {
            if(!(node instanceof NameExpr)) return false;
            var expr = (NameExpr)node;
            return expr.getName().asString().equals(this.name);
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ClassLiteral extends Expression {
        public final Location location;
        public final List<String> packageFragments;
        public final String simpleName;

        @Override
        public boolean testNode(Node node) {
            if(!(node instanceof com.github.javaparser.ast.expr.ClassExpr)) return false;
            var expr = (ClassExpr)node;
            if(expr.getType().isClassOrInterfaceType()) {
                if(packageFragments.size() == 0) {
                    if(!((ClassOrInterfaceType)expr.getType()).getNameAsString().equals(simpleName)) {
                        return false;
                    }
                } else {
                    if(!((ClassOrInterfaceType)expr.getType()).getNameAsString().equals(
                            packageFragments.stream().reduce(
                                    (l, r) -> l + r
                            ) + "." + simpleName
                    )) {
                        return false;
                    }
                }
                return false;
            } else if(expr.getType().isPrimitiveType()) {
                if(!((PrimitiveType)expr.getType()).getType().asString().equals(simpleName)) return false;
            }
            return true;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ArrayAccessExpression extends Expression {
        public final Location location;
        public final Expression lhs;
        public final Expression rhs;

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof ArrayAccessExpr)) return false;
            var expr = (ArrayAccessExpr) node;
            if (!lhs.testNode(expr.getName())) return false;
            return rhs.testNode(expr.getIndex());
        }

    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static abstract class UnaryExpression extends Expression {
        public final Location location;
        public final Expression expression;
    }

    public static class UnaryPlusExpression extends UnaryExpression {
        public UnaryPlusExpression(Location location, Expression expression) {
            super(location, expression);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof UnaryExpr)) return false;
            var expr = (UnaryExpr) node;
            if(!expression.testNode(expr)) return false;
            if(!expr.getOperator().equals(UnaryExpr.Operator.PLUS)) return false;
            return true;
        }


    }

    public static class UnaryMinusExpression extends UnaryExpression {
        public UnaryMinusExpression(Location location, Expression expression) {
            super(location, expression);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof UnaryExpr)) return false;
            var expr = (UnaryExpr) node;
            if(!expression.testNode(expr)) return false;
            if(!expr.getOperator().equals(UnaryExpr.Operator.MINUS)) return false;
            return true;
        }
    }

    public static class LogicalComplementExpression extends UnaryExpression {
        public LogicalComplementExpression(Location location, Expression expression) {
            super(location, expression);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof UnaryExpr)) return false;
            var expr = (UnaryExpr) node;
            if(!expression.testNode(expr)) return false;
            if(!expr.getOperator().equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT)) return false;
            return true;
        }

    }

    public static class BitwiseComplementExpression extends UnaryExpression {
        public BitwiseComplementExpression(Location location, Expression expression) {
            super(location, expression);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof UnaryExpr)) return false;
            var expr = (UnaryExpr) node;
            if(!expression.testNode(expr)) return false;
            if(!expr.getOperator().equals(UnaryExpr.Operator.BITWISE_COMPLEMENT)) return false;
            return true;
        }

    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static abstract class BinaryExpression extends Expression {
        public final Location location;
        public final String symbol;
        public final Expression lhs;
        public final Expression rhs;
    }

    public static abstract class RelationalExpression extends BinaryExpression {
        public RelationalExpression(Location location, String symbol, Expression lhs, Expression rhs) {
            super(location, symbol, lhs, rhs);
        }
    }

    public static abstract class AssignmentExpression extends BinaryExpression {
        public AssignmentExpression(Location location, String symbol, Expression lhs, Expression rhs) {
            super(location, symbol, lhs, rhs);
        }
    }

    public static class SimpleAssignment extends BinaryExpression {
        public SimpleAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.ASSIGN)) return false;
            return true;
        }

    }

    public static class AdditionAssignment extends BinaryExpression {
        public AdditionAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "+=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.PLUS)) return false;
            return true;
        }

    }

    public static class SubtractionAssignment extends BinaryExpression {
        public SubtractionAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "-=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.MINUS)) return false;
            return true;
        }

    }

    public static class MultiplicationAssignment extends BinaryExpression {
        public MultiplicationAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.ASSIGN)) return false;
            return true;
        }

    }

    public static class DivisionAssignment extends BinaryExpression {
        public DivisionAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "/=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.DIVIDE)) return false;
            return true;
        }

    }

    public static class RemainderAssignment extends BinaryExpression {
        public RemainderAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "%=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.REMAINDER)) return false;
            return true;
        }

    }

    public static class BitwiseAndAssignment extends BinaryExpression {
        public BitwiseAndAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "&=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.BINARY_AND)) return false;
            return true;
        }

    }

    public static class BitwiseOrAssignment extends BinaryExpression {
        public BitwiseOrAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "|=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.BINARY_OR)) return false;
            return true;
        }

    }

    public static class XorAssignment extends BinaryExpression {
        public XorAssignment(Location location, Expression lhs, Expression rhs) {
            super(location, "^=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof AssignExpr)) return false;
            var expr = (AssignExpr) node;
            if(!lhs.testNode(expr.getTarget())) return false;
            if(!rhs.testNode(expr.getValue())) return false;
            if(!expr.getOperator().equals(AssignExpr.Operator.XOR)) return false;
            return true;
        }

    }

    public static class Addition extends BinaryExpression {
        public Addition(Location location, Expression lhs, Expression rhs) {
            super(location, "+", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.PLUS)) return false;
            return true;
        }

    }

    public static class BitwiseAndExpression extends BinaryExpression {
        public BitwiseAndExpression(Location location, Expression lhs, Expression rhs) {
            super(location, "&", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.BINARY_AND)) return false;
            return true;
        }

    }

    public static class BitwiseOrExpression extends BinaryExpression {
        public BitwiseOrExpression(Location location, Expression lhs, Expression rhs) {
            super(location, "|", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.BINARY_OR)) return false;
            return true;
        }

    }

    public static class XorExpression extends BinaryExpression {
        public XorExpression(Location location, Expression lhs, Expression rhs) {
            super(location, "^", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.XOR)) return false;
            return true;
        }

    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ConditionalExpression extends Expression {
        public final Location location;
        public final Expression condition;
        public final Expression thenPart;
        public final Expression elsepart;

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof ConditionalExpr)) return false;
            var expr = (ConditionalExpr) node;
            if(!condition.testNode(expr.getCondition())) return false;
            if(!thenPart.testNode(expr.getThenExpr())) return false;
            if(!elsepart.testNode(expr.getElseExpr())) return false;
            return true;
        }

    }

    public static class ConditionalAndExpression extends BinaryExpression {
        public ConditionalAndExpression(Location location, Expression lhs, Expression rhs) {
            super(location, "&&", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.AND)) return false;
            return true;
        }

    }

    public static class ConditionalOrExpression extends BinaryExpression {
        public ConditionalOrExpression(Location location, Expression lhs, Expression rhs) {
            super(location, "||", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.OR)) return false;
            return true;
        }

    }

    public static class Subtraction extends BinaryExpression {
        public Subtraction(Location location, Expression lhs, Expression rhs) {
            super(location, "-", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.MINUS)) return false;
            return true;
        }

    }

    public static class Multiplication extends BinaryExpression {
        public Multiplication(Location location, Expression lhs, Expression rhs) {
            super(location, "*", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.MULTIPLY)) return false;
            return true;
        }

    }

    public static class Division extends BinaryExpression {
        public Division(Location location, Expression lhs, Expression rhs) {
            super(location, "/", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.DIVIDE)) return false;
            return true;
        }

    }

    public static class Remainder extends BinaryExpression {
        public Remainder(Location location, Expression lhs, Expression rhs) {
            super(location, "%", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.REMAINDER)) return false;
            return true;
        }
    }

    public static class Equal extends BinaryExpression {
        public Equal(Location location, Expression lhs, Expression rhs) {
            super(location, "==", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.EQUALS)) return false;
            return true;
        }
    }

    public static class NotEqual extends BinaryExpression {
        public NotEqual(Location location, Expression lhs, Expression rhs) {
            super(location, "!=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.NOT_EQUALS)) return false;
            return true;
        }
    }

    @ToString
    public static class GreaterOrEqual extends RelationalExpression {
        public GreaterOrEqual(Location location, Expression lhs, Expression rhs) {
            super(location, ">=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.GREATER_EQUALS)) return false;
            return true;
        }
    }

    public static class GreaterThan extends RelationalExpression {
        public GreaterThan(Location location, Expression lhs, Expression rhs) {
            super(location, ">", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.GREATER)) return false;
            return true;
        }
    }

    public static class LessOrEqual extends RelationalExpression {
        public LessOrEqual(Location location, Expression lhs, Expression rhs) {
            super(location, "<=", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.LESS_EQUALS)) return false;
            return true;
        }
    }

    public static class LessThan extends RelationalExpression {
        public LessThan(Location location, Expression lhs, Expression rhs) {
            super(location, "<", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.LESS)) return false;
            return true;
        }
    }

    public static class LeftShiftExpression extends RelationalExpression {
        public LeftShiftExpression(Location location, Expression lhs, Expression rhs) {
            super(location, "<<", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.LEFT_SHIFT)) return false;
            return true;
        }
    }

    public static class RightShiftExpression extends RelationalExpression {
        public RightShiftExpression(Location location, Expression lhs, Expression rhs) {
            super(location, ">>", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.SIGNED_RIGHT_SHIFT)) return false;
            return true;
        }
    }

    public static class UnsignedRightShiftExpression extends RelationalExpression {
        public UnsignedRightShiftExpression(Location location, Expression lhs, Expression rhs) {
            super(location, ">>>", lhs, rhs);
        }

        @Override
        public boolean testNode(Node node) {
            if (!(node instanceof BinaryExpr)) return false;
            var expr = (BinaryExpr) node;
            if(!lhs.testNode(expr.getLeft())) return false;
            if(!rhs.testNode(expr.getRight())) return false;
            if(!expr.getOperator().equals(BinaryExpr.Operator.UNSIGNED_RIGHT_SHIFT)) return false;
            return true;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class InstanceofExpression extends Expression {
        public final Location location;
        public final Expression target;
        public final String type;


        @Override
        public boolean testNode(Node node) {
            if(node instanceof InstanceOfExpr) {
                var instanceofExpr = (InstanceOfExpr)node;
                if(target.testNode(instanceofExpr.getExpression())) return false;
                if(!instanceofExpr.getType().asClassOrInterfaceType().getNameAsString().equals(type)) return false;
                return true;
            } else {
                return false;
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class InstanceCreationExpression extends Expression {
        public final Location location;
        public final String name;
        public final List<Expression> parameters;

        private boolean testArgs(NodeList<com.github.javaparser.ast.expr.Expression> parameters) {
            if(this.parameters.size() == 1 && this.parameters.get(0) instanceof RepeatedParameter) return true;
            if(this.parameters.size() != parameters.size()) return false;
            int size = parameters.size();
            for(int i = 0; i < size; i++) {
                var p1 = this.parameters.get(i);
                var p2 = parameters.get(i);
                if(!p1.testNode(p2)) return false;
            }
            return true;
        }

        @Override
        public boolean testNode(Node node) {
            if(node instanceof ObjectCreationExpr) {
                var newObject = (ObjectCreationExpr)node;
                if(!newObject.getType().getNameAsString().equals(name)) return false;
                if(!testArgs(((ObjectCreationExpr) node).getArguments())) return false;
                return true;
            } else {
                return false;
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ArrayCreationExpression extends Expression {
        public final Location location;
        public final String name;
        public final List<Expression> levels;

        private boolean testLevels(NodeList<ArrayCreationLevel> levels) {
            if(this.levels.size() == 1 && this.levels.get(0) instanceof RepeatedParameter) return true;
            if(this.levels.size() != levels.size()) return false;
            int size = levels.size();
            for(int i = 0; i < size; i++) {
                var l1 = this.levels.get(i);
                var l2 = levels.get(i);
                if(!l1.testNode(l2)) return false;
            }
            return true;
        }

        @Override
        public boolean testNode(Node node) {
            if(node instanceof ArrayCreationExpr) {
                var newArray= (ArrayCreationExpr)node;
                var elementTYpe = newArray.getElementType();
                if(!elementTYpe.isClassOrInterfaceType()) {
                // new Object[10], String[20],　...
                    if(!elementTYpe.asClassOrInterfaceType().getNameAsString().equals(name)) return false;
                } else {
                // new int[3], new float[4], ...
                    if(!elementTYpe.asPrimitiveType().getType().asString().equals(name)) return false;
                }
                if(!testLevels(newArray.getLevels())) return false;
                return true;
            } else {
                return false;
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class MethodCall extends Expression {
        public final Location location;
        public final Expression receiver;
        public final String name;
        public final List<Expression> parameters;

        private boolean testReceiver(Optional<com.github.javaparser.ast.expr.Expression> receiver) {
            if(this.receiver != null && !receiver.isPresent()) return false;
            if(this.receiver == null && receiver.isPresent()) return false;
            if(this.receiver == null) {
                return true;
            } else {
                return this.receiver.testNode(receiver.get());
            }
        }

        private boolean testArgs(NodeList<com.github.javaparser.ast.expr.Expression> parameters) {
            if(this.parameters.size() == 1 && this.parameters.get(0) instanceof RepeatedParameter) return true;
            if(this.parameters.size() != parameters.size()) return false;
            int size = parameters.size();
            for(int i = 0; i < size; i++) {
                var p1 = this.parameters.get(i);
                var p2 = parameters.get(i);
                if(!p1.testNode(p2)) return false;
            }
            return true;
        }

        @Override
        public boolean testNode(Node node) {
            if(node instanceof MethodCallExpr) {
                var call = (MethodCallExpr)node;
                if(!call.getName().asString().equals(name)) return false;
                if(!testReceiver(((MethodCallExpr) node).getScope())) return false;
                if(!testArgs(((MethodCallExpr) node).getArguments())) return false;
                return true;
            } else {
                return false;
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class FunctionCall extends Expression {
        public final Location location;
        public final String name;
        public final List<Expression> parameters;

        private boolean testArgs(NodeList<com.github.javaparser.ast.expr.Expression> parameters) {
            if(this.parameters.size() == 1 && this.parameters.get(0) instanceof RepeatedParameter) return true;
            if(this.parameters.size() != parameters.size()) return false;
            int size = parameters.size();
            for(int i = 0; i < size; i++) {
                var p1 = this.parameters.get(i);
                var p2 = parameters.get(i);
                if(!p1.testNode(p2)) return false;
            }
            return true;
        }

        @Override
        public boolean testNode(Node node) {
            if(node instanceof MethodCallExpr) {
                var call = (MethodCallExpr)node;
                if(call.getScope().isPresent()) return false;
                if(!call.getName().asString().equals(name)) return false;
                if(!testArgs(((MethodCallExpr) node).getArguments())) return false;
                return true;
            } else {
                return false;
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class FieldSelection extends Expression {
        public final Location location;
        public final Expression receiver;
        public final String name;

        @Override
        public boolean testNode(Node node) {
            if(!(node instanceof FieldAccessExpr)) return false;
            var expr = (FieldAccessExpr)node;
            if(!this.name.equals(expr.getNameAsString())) return false;
            return this.receiver.testNode(expr.getScope());
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class PrefixIncrementExpression extends Expression {
        public final Location location;
        public final Expression target;

        @Override
        public boolean testNode(Node node) {
            if(!(node instanceof UnaryExpr)) return false;
            var expr = (UnaryExpr)node;
            if(!expr.getOperator().equals(UnaryExpr.Operator.PREFIX_INCREMENT)) return false;
            return this.target.testNode(expr.getExpression());
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class PrefixDecrementExpression extends Expression {
        public final Location location;
        public final Expression target;

        @Override
        public boolean testNode(Node node) {
            if(!(node instanceof UnaryExpr)) return false;
            var expr = (UnaryExpr)node;
            if(!expr.getOperator().equals(UnaryExpr.Operator.PREFIX_DECREMENT)) return false;
            return this.target.testNode(expr.getExpression());
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class PostIncrement extends Expression {
        public final Location location;
        public final Expression target;

        @Override
        public boolean testNode(Node node) {
            if(!(node instanceof UnaryExpr)) return false;
            var expr = (UnaryExpr)node;
            if(!expr.getOperator().equals(UnaryExpr.Operator.POSTFIX_INCREMENT)) return false;
            return this.target.testNode(expr.getExpression());
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class PostDecrement extends Expression {
        public final Location location;
        public final Expression target;

        @Override
        public boolean testNode(Node node) {
            if(!(node instanceof UnaryExpr)) return false;
            var expr = (UnaryExpr)node;
            if(!expr.getOperator().equals(UnaryExpr.Operator.POSTFIX_DECREMENT)) return false;
            return this.target.testNode(expr.getExpression());
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class Wildcard extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return true;
        }
    }


    @AllArgsConstructor
    @Getter
    @ToString
    public static class ThisLiteral extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return node instanceof ThisExpr;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class NullLiteral extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return node instanceof NullLiteralExpr;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class IntLiteral extends Expression {
        public final Location location;
        public final int value;

        @Override
        public boolean testNode(Node node) {
            if(node instanceof IntegerLiteralExpr) {
                return value == ((IntegerLiteralExpr)node).asInt();
            }
            return false;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class IntWildcard extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return node instanceof IntegerLiteralExpr;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class DoubleLiteral extends Expression {
        public final Location location;
        public final double value;

        @Override
        public boolean testNode(Node node) {
            if(node instanceof DoubleLiteralExpr) {
                return value == ((DoubleLiteralExpr)node).asDouble();
            }
            return false;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class DoubleWildcard extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return node instanceof DoubleLiteralExpr;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class LambdaPattern extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return node instanceof LambdaExpr;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class BooleanLiteral extends Expression {
        public final Location location;
        public final boolean value;

        @Override
        public boolean testNode(Node node) {
            if(node instanceof BooleanLiteralExpr) {
                return value == ((BooleanLiteralExpr)node).getValue();
            }
            return false;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class BooleanWildcard extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return node instanceof BooleanLiteralExpr;
        }
    }


    @AllArgsConstructor
    @Getter
    @ToString
    public static class StringLiteral extends Expression {
        public final Location location;
        public final String value;

        @Override
        public boolean testNode(Node node) {
            if(node instanceof StringLiteralExpr) {
                return value.equals(((StringLiteralExpr)node).getValue());
            }
            return false;
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class StringWildcard extends Expression {
        public final Location location;

        @Override
        public boolean testNode(Node node) {
            return node instanceof StringLiteralExpr;
        }
    }
}
