package parsing.ast;

import java.util.function.BiFunction;

public class DoWhileLoop extends Loop {

    public DoWhileLoop(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return body;
        case 1: return condition;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public String print(String indentation) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(idComment()).append(indentation).append("do");
        
        if (body instanceof Block) {
            sb.append(" ").append(body.print(indentation).substring(indentation.length()));
        } else {
            sb.append("\n").append(body.print(indentation + "\t"));
        }
        
        if (body instanceof Block) {
            // replace trailing \n with " "
            sb.delete(sb.length() - 1, sb.length());
            sb.append(" ");
        } else {
            sb.append(indentation);
        }
        
        sb.append("while (").append(condition.print(indentation)).append(");\n");
        
        return sb.toString();
    }
    
    @Override
    public String getText() {
        return "do " + body.getText() + " while (" + condition.getText() + ");";
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visitDoWhileLoop(this);
    }

    @Override
    public DoWhileLoop cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        DoWhileLoop clone = new DoWhileLoop(parent);
        
        clone.body = (Statement) cloneFct.apply(body, clone);
        clone.condition = (Expression) cloneFct.apply(condition, clone);
        
        return clone;
    }
    
}
