package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class Identifier extends Expression {

    public String identifier;
    
    public Identifier(AstElement parent) {
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
    public int getPrecedence() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitIdentifier(this);
    }

    @Override
    public Identifier cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        Identifier clone = new Identifier(parent);
        
        clone.identifier = identifier;
        
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Identifier other = (Identifier) obj;
            equals = identifier.equals(other.identifier);
        }
        return equals;
    }

}
