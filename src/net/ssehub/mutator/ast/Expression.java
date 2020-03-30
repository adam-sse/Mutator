package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IAstVisitor;
import net.ssehub.mutator.ast.operations.IExpressionVisitor;

public abstract class Expression extends AstElement {

    public Expression(AstElement parent) {
        super(parent);
    }

    public abstract <T> T accept(IExpressionVisitor<T> visitor);

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return accept((IExpressionVisitor<T>) visitor);
    }

    /**
     * Used for printing parenthesis only. Higher precedence means evaluated first.
     * That means, an argument with lower or equal precedence needs parenthesis.
     */
    public abstract int getPrecedence();

}
