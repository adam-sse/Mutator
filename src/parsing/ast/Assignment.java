package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class Assignment extends Statement {
    
    public Expression variable;
    
    public Expression value;

    public Assignment(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return variable;
        case 1: return value;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return 2;
    }
    
    @Override
    public String print(String indentation) {
        return idComment() + indentation + getText() + "\n";
    }
    
    @Override
    public String getText() {
        return variable.getText() + " = " + value.getText() + ";";
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }
    
    @Override
    public Assignment cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        Assignment clone = new Assignment(parent);
        
        clone.variable = (Expression) cloneFct.apply(variable, clone);
        clone.value = (Expression) cloneFct.apply(value, clone);
        
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Assignment other = (Assignment) obj;
            equals = variable.equals(other.variable) && value.equals(other.value);
        }
        return equals;
    }
    
}
