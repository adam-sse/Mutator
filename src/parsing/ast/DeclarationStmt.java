package parsing.ast;

import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

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
    public String print(String indentation) {
        return idComment() + indentation + getText() + "\n";
    }
    
    @Override
    public String getText() {
        return decl.getText() + ";";
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitDeclarationStmt(this);
    }

    @Override
    public DeclarationStmt cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        DeclarationStmt clone = new DeclarationStmt(parent);
        
        clone.decl = (Declaration) cloneFct.apply(decl, clone);
        
        return clone;
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
    
}
