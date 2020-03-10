package net.ssehub.mutator.parsing.ast;

import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

public class Assignment extends Statement {
    
    public Expression variable;
    
    public Expression value;

    public Assignment(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return variable;
        case 1: return value;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return 2;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Assignment other = (Assignment) obj;
            equals = variable.equals(other.variable) && value.equals(other.value);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 13 * variable.hashCode() + 31 * value.hashCode();
    }
    
}
