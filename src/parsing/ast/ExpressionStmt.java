package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

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
    public ExpressionStmt cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        ExpressionStmt clone = new ExpressionStmt(parent);
        
        clone.expr = (Expression) cloneFct.apply(expr, clone);
        
        return clone;
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
    
}
