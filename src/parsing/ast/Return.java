package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class Return extends Statement {

    public Expression value;
    
    public Return(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: if (value != null) return value; else throw new IndexOutOfBoundsException(index);
        default: throw new IndexOutOfBoundsException(index);
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
    public Return cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        Return clone = new Return(parent);
        
        if (value != null) {
            clone.value = (Expression) cloneFct.apply(value, clone);
        }
        
        return clone;
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
