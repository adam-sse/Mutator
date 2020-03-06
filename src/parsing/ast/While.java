package parsing.ast;

import java.util.function.BiFunction;

public class While extends Loop {

    public While(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return condition;
        case 1: return body;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public String print(String indentation) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(idComment()).append(indentation).append("while (").append(condition.print(indentation)).append(")");
        
        if (body instanceof Block) {
            sb.append(" ").append(body.print(indentation).substring(indentation.length()));
        } else {
            sb.append("\n").append(body.print(indentation + "\t"));
        }
        
        return sb.toString();
    }
    
    @Override
    public String getText() {
        return "while (" + condition.getText() + ") " + body.getText();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visitWhile(this);
    }

    @Override
    public While cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        While clone = new While(parent);
        
        clone.condition = (Expression) cloneFct.apply(condition, clone);
        clone.body = (Statement) cloneFct.apply(body, clone);
        
        return clone;
    }

}
