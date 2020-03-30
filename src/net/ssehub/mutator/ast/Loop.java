package net.ssehub.mutator.ast;

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
            equals = body.equals(other.body);
            if (condition != null) {
                equals &= condition.equals(other.condition);
            } else {
                equals &= other.condition == null;
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return (condition != null ? 293 * condition.hashCode() : 13) + 509 * body.hashCode();
    }

}
