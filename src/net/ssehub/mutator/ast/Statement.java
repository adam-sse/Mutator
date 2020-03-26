package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IAstVisitor;
import net.ssehub.mutator.ast.operations.IStatementVisitor;

public abstract class Statement extends AstElement {

    public Statement(AstElement parent) {
        super(parent);
    }
    
    public abstract <T> T accept(IStatementVisitor<T> visitor);
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return accept((IStatementVisitor<T>) visitor);
    }
    
}
