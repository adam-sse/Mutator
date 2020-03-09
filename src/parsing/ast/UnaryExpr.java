package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

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
    public UnaryExpr cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        UnaryExpr clone = new UnaryExpr(parent);
        
        clone.expr = (Expression) cloneFct.apply(expr, clone);
        clone.operator = operator;
        
        return clone;
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

}
