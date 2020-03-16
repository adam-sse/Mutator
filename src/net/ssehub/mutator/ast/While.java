package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IAstVisitor;

public class While extends Loop {

    public While(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return condition;
        case 1: return body;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitWhile(this);
    }

}
