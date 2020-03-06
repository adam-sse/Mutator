package parsing.ast;

import java.util.function.BiFunction;

public abstract class Expression extends AstElement {

    public Expression(AstElement parent) {
        super(parent);
    }

    /**
     * Used for printing parenthesis only. Higher precedence means evaluated first. That means,
     * an argument with lower or equal precedence needs parenthesis.
     */
    abstract int getPrecedence();
    
    @Override
    public abstract Expression cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct);
    
    
    @Override
    public String print(String indentation) {
        return getText();
    }
    
}
