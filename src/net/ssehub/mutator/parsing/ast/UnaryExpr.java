package net.ssehub.mutator.parsing.ast;

import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

public class UnaryExpr extends Expression {
    
    public Expression expr;
    
    public UnaryOperator operator;
    
    public UnaryExpr(AstElement parent) {
        super(parent);
    }
    
    @Override
    public int getPrecedence() {
        return operator.precedence;
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return expr;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return 0;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitUnaryExpr(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            UnaryExpr other = (UnaryExpr) obj;
            equals = operator == other.operator && expr.equals(other.expr);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 31 * operator.hashCode() + 233 * expr.hashCode();
    }

}
