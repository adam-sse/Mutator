package parsing.ast;

import java.util.function.BiFunction;

public abstract class Loop extends Statement {

    public Loop(AstElement parent) {
        super(parent);
    }

    public Expression condition;
    
    public Statement body;
    
    @Override
    public int getNumChildren() {
        return 2;
    }
    
    @Override
    public abstract Loop cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct);
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Loop other = (Loop) obj;
            equals = condition.equals(other.condition) && body.equals(other.body);
        }
        return equals;
    }
    
}
