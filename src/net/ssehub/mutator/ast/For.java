package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IAstVisitor;

public class For extends Loop {

    public Declaration init;
    
    public Expression increment;
    
    public For(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("not yet implemented for ForLoop");
    }
    
    @Override
    public int getNumChildren() {
        throw new UnsupportedOperationException("not yet implemented for ForLoop");
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitFor(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            For other = (For) obj;
            equals = true;
            
            if (this.init != null) {
                equals &= this.init.equals(other.init);
            } else {
                equals &= other.init == null;
            }
            
            if (this.increment != null) {
                equals &= this.increment.equals(other.increment);
            } else {
                equals &= other.increment == null;
            }
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() + (init != null ? 269 * init.hashCode() : 223)
                + (increment != null ? 379 * increment.hashCode() : 487);
    }

}
