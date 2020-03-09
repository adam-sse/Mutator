package parsing.ast;

import parsing.ast.operations.IAstVisitor;

public class Type extends AstElement {

    public enum Modifier {
        SIGNED,
        UNSIGNED;
    }
    
    public BasicType type;
    
    public boolean pointer;
    
    public Modifier modifier;
    
    public Type(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException(index);
    }
    
    @Override
    public int getNumChildren() {
        return 0;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitType(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Type other = (Type) obj;
            equals = type == other.type && pointer == other.pointer && modifier == other.modifier;
        }
        return equals;
    }
    
}
