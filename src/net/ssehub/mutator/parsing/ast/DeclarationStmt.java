package net.ssehub.mutator.parsing.ast;

import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

public class DeclarationStmt extends Statement {

    public Declaration decl;
    
    public DeclarationStmt(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return decl;
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return 1;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitDeclarationStmt(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            DeclarationStmt other = (DeclarationStmt) obj;
            equals = decl.equals(other.decl);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 349 * decl.hashCode();
    }
    
}
