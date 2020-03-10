package net.ssehub.mutator.parsing.ast;

import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

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
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Declaration other = (Declaration) obj;
            equals = type.equals(other.type) && identifier.equals(other.identifier);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 397 * type.hashCode() + 173 * identifier.hashCode();
    }

}
