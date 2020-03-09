package parsing.ast;

import parsing.ast.operations.IAstVisitor;

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
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitEmptyStmt(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
}
