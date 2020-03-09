package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class If extends Statement {

    public Expression condition;
    
    public Statement thenBlock;
    
    public Statement elseBlock;
    
    public If(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return condition;
        case 1: return thenBlock;
        case 2: if (elseBlock != null) return elseBlock; else throw new IndexOutOfBoundsException(index);
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return elseBlock != null ? 3 : 2;
    }
    
    @Override
    public String print(String indentation) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(idComment()).append(indentation).append("if (").append(condition.print(indentation)).append(")");
        
        if (thenBlock instanceof Block) {
            sb.append(" ").append(thenBlock.print(indentation).substring(indentation.length()));
        } else {
            sb.append("\n").append(thenBlock.print(indentation + "\t"));
        }
        
        if (elseBlock != null) {
            sb.append(indentation).append("else");
            
            if (elseBlock instanceof Block) {
                sb.append(" ").append(elseBlock.print(indentation).substring(indentation.length()));
            } else {
                sb.append("\n").append(elseBlock.print(indentation + "\t"));
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String getText() {
        String result = "if (" + condition.getText() + ") " + thenBlock.getText();
        if (elseBlock != null) {
            result += " else " + elseBlock.getText();
        }
        return result;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitIf(this);
    }

    @Override
    public If cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        If clone = new If(parent);
        
        clone.condition = (Expression) cloneFct.apply(condition, clone);
        clone.thenBlock = (Statement) cloneFct.apply(thenBlock, clone);
        if (elseBlock != null) {
            clone.elseBlock = (Statement) cloneFct.apply(elseBlock, clone);
        }
        
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            If other = (If) obj;
            equals = condition.equals(other.condition) && thenBlock.equals(other.thenBlock);
            if (this.elseBlock == null) {
                equals &= other.thenBlock == null;
            } else {
                equals &= thenBlock.equals(other.thenBlock);
            }
        }
        return equals;
    }
    
}
