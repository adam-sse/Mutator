package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class Literal extends Expression {

    public String value;
    
    public Literal(AstElement parent) {
        super(parent);
    }
    
    @Override
    public int getPrecedence() {
        return Integer.MAX_VALUE;
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
        return visitor.visitLiteral(this);
    }

    @Override
    public Literal cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        Literal clone = new Literal(parent);
        
        clone.value = value;
        
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Literal other = (Literal) obj;
            equals = value.equals(other.value);
        }
        return equals;
    }
    
}
