package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IAstVisitor;

public class ExpressionStmt extends Statement {

    public Expression expr;
    
    public ExpressionStmt(AstElement parent) {
        super(parent);
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
        return 1;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitExpressionStmt(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            ExpressionStmt other = (ExpressionStmt) obj;
            equals = expr.equals(other.expr);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 149 * expr.hashCode();
    }
    
}
