package parsing.ast;

import java.util.function.BiFunction;

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
    int getPrecedence() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public String getText() {
        return identifier;
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visitIdentifier(this);
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
