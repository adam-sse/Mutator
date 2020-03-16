package net.ssehub.mutator.ast;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.ast.operations.IAstVisitor;

public class FunctionCall extends Expression {

    public String function;
    
    public List<Expression> params = new LinkedList<>();
    
    public FunctionCall(AstElement parent) {
        super(parent);
    }
    
    @Override
    public int getPrecedence() {
        return 12;
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        return params.get(index);
    }
    
    @Override
    public int getNumChildren() {
        return params.size();
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            FunctionCall other = (FunctionCall) obj;
            equals = function.equals(other.function) && params.equals(other.params);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 281 * function.hashCode() + 521 * params.hashCode();
    }
    
}
