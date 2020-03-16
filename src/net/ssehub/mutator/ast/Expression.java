package net.ssehub.mutator.ast;

public abstract class Expression extends AstElement {

    public Expression(AstElement parent) {
        super(parent);
    }

    /**
     * Used for printing parenthesis only. Higher precedence means evaluated first. That means,
     * an argument with lower or equal precedence needs parenthesis.
     */
    public abstract int getPrecedence();
    
}
