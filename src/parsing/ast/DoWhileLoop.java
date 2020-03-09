package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class DoWhileLoop extends Loop {

    public DoWhileLoop(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return body;
        case 1: return condition;
        default: throw new IndexOutOfBoundsException(index);
        }
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitDoWhileLoop(this);
    }

    @Override
    public DoWhileLoop cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        DoWhileLoop clone = new DoWhileLoop(parent);
        
        clone.body = (Statement) cloneFct.apply(body, clone);
        clone.condition = (Expression) cloneFct.apply(condition, clone);
        
        return clone;
    }
    
}
