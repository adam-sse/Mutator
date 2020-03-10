package net.ssehub.mutator.parsing.ast;

public abstract class Loop extends Statement {


    public Expression condition;
    
    public Statement body;
    
    public Loop(AstElement parent) {
        super(parent);
    }
    
    @Override
    public int getNumChildren() {
        return 2;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Loop other = (Loop) obj;
            equals = condition.equals(other.condition) && body.equals(other.body);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return 293 * condition.hashCode() + 509 * body.hashCode();
    }
    
}
