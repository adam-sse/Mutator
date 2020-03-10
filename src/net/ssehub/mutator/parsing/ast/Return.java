package net.ssehub.mutator.parsing.ast;

import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

public class Return extends Statement {

    public Expression value;
    
    public Return(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        if (index == 0 && value != null) {
            return value;
        } else {
            throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return value != null ? 1 : 0;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitReturn(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Return other = (Return) obj;
            if (this.value != null) {
                equals = value.equals(other.value);
            } else {
                equals = other.value == null;
            }
        }
        return equals;
    }
    
}
