package net.ssehub.mutator.parsing.ast;

import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

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
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Literal other = (Literal) obj;
            equals = value.equals(other.value);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 199 * value.hashCode();
    }
    
}
