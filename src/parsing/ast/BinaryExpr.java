package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class BinaryExpr extends Expression {
    
    public Expression left;
    
    public BinaryOperator operator;
    
    public Expression right;
    
    public BinaryExpr(AstElement parent) {
        super(parent);
    }

    @Override
    public int getPrecedence() {
        return operator.precedence;
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return left;
        case 1: return right;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return 2;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitBinaryExpr(this);
    }
    
    @Override
    public BinaryExpr cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        BinaryExpr clone = new BinaryExpr(parent);
        
        clone.left = (Expression) cloneFct.apply(left, clone);
        clone.operator = operator;
        clone.right = (Expression) cloneFct.apply(right, clone);
        
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            BinaryExpr other = (BinaryExpr) obj;
            equals = operator == other.operator && left.equals(other.left) && right.equals(other.right);
        }
        return equals;
    }

}
