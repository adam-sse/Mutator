package parsing.ast;

import java.util.function.BiFunction;

public class EmptyStmt extends Statement {

    public EmptyStmt(AstElement parent) {
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
    public String print(String indentation) {
        return idComment() + indentation + ";\n";
    }
    
    @Override
    public String getText() {
        return ";";
    }

    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visitEmptyStmt(this);
    }
    
    @Override
    public EmptyStmt cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        EmptyStmt clone = new EmptyStmt(parent);
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
}
