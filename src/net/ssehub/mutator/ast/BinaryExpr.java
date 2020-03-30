package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IExpressionVisitor;

public class BinaryExpr extends Expression {

    public Expression left;

    public BinaryOperator operator;

    public Expression right;

    public BinaryExpr(AstElement parent) {
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
            return this.left;

        case 1:
            return this.right;

        default:
            throw new IndexOutOfBoundsException(index);
        }
    }

    @Override
    public int getNumChildren() {
        return 2;
    }

    @Override
    public <T> T accept(IExpressionVisitor<T> visitor) {
        return visitor.visitBinaryExpr(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            BinaryExpr other = (BinaryExpr) obj;
            equals = this.operator == other.operator && this.left.equals(other.left) && this.right.equals(other.right);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return this.operator.hashCode() + 179 * this.left.hashCode() + 59 * this.right.hashCode();
    }

}
