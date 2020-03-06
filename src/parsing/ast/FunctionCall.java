package parsing.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BiFunction;

public class FunctionCall extends Expression {

    public String function;
    
    public List<Expression> params = new LinkedList<>();
    
    public FunctionCall(AstElement parent) {
        super(parent);
    }
    
    @Override
    int getPrecedence() {
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
    public String getText() {
        StringJoiner sj = new StringJoiner(", ", function + "(", ")");
        for (Expression param : params) {
            sj.add(param.getText());
        }
        return sj.toString();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visitFunctionCall(this);
    }

    @Override
    public FunctionCall cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        FunctionCall clone = new FunctionCall(parent);
        
        clone.function = function;
        for (Expression param : params) {
            clone.params.add((Expression) cloneFct.apply(param, clone));
        }
        
        return clone;
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
    
}
