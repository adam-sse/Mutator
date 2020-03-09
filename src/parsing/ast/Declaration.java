package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class Declaration extends AstElement {
    
    public Type type;
    
    public String identifier;
    
    public Declaration(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return type;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return 1;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitDeclaration(this);
    }

    @Override
    public Declaration cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        Declaration clone = new Declaration(parent);
        
        clone.type = (Type) cloneFct.apply(type, clone);
        clone.identifier = identifier;
        
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Declaration other = (Declaration) obj;
            equals = type.equals(other.type) && identifier.equals(other.identifier);
        }
        return equals;
    }

}
