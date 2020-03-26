package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IStatementVisitor;

public class If extends Statement {

    public Expression condition;
    
    public Statement thenBlock;
    
    public Statement elseBlock;
    
    public If(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return condition;
        case 1: return thenBlock;
        case 2:
            if (elseBlock != null) {
                return elseBlock;
            } else {
                throw new IndexOutOfBoundsException(index);
            }
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return elseBlock != null ? 3 : 2;
    }
    
    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visitIf(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            If other = (If) obj;
            equals = condition.equals(other.condition) && thenBlock.equals(other.thenBlock);
            if (this.elseBlock == null) {
                equals &= other.thenBlock == null;
            } else {
                equals &= thenBlock.equals(other.thenBlock);
            }
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 401 * condition.hashCode() + 353 * thenBlock.hashCode()
                + (elseBlock != null ? 373 * elseBlock.hashCode() : 13);
    }
    
}
