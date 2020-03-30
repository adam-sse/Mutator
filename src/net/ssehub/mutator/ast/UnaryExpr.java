package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IExpressionVisitor;

public class UnaryExpr extends Expression {

    public Expression expr;

    public UnaryOperator operator;

    public UnaryExpr(AstElement parent) {
        super(parent);
    }

    @Override
    public int getPrecedence() {
        return this.operator.precedence;
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0:
            return this.expr;

        default:
            throw new IndexOutOfBoundsException(index);
        }
    }

    @Override
    public int getNumChildren() {
        return 0;
    }

    @Override
    public <T> T accept(IExpressionVisitor<T> visitor) {
        return visitor.visitUnaryExpr(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            UnaryExpr other = (UnaryExpr) obj;
            equals = this.operator == other.operator && this.expr.equals(other.expr);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return 31 * this.operator.hashCode() + 233 * this.expr.hashCode();
    }

}
