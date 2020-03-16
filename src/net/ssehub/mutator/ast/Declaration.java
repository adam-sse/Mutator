package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IAstVisitor;

public class Declaration extends AstElement {
    
    public Type type;
    
    public String identifier;
    
    public Expression initExpr;
    
    public Declaration(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0: return type;
        case 1: if (initExpr != null) return initExpr; else throw new IndexOutOfBoundsException(index);
        default: throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return initExpr != null ? 2 : 1;
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitDeclaration(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Declaration other = (Declaration) obj;
            equals = type.equals(other.type) && identifier.equals(other.identifier);
            if (initExpr != null) {
                equals &= initExpr.equals(other.initExpr);
            } else {
                equals &= other.initExpr == null;
            }
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 397 * type.hashCode() + 173 * identifier.hashCode() + (initExpr != null ? 107 * initExpr.hashCode() : 0);
    }

}
