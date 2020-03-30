package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IStatementVisitor;

public class While extends Loop {

    public While(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0:
            return this.condition;

        case 1:
            return this.body;

        default:
            throw new IndexOutOfBoundsException(index);
        }
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visitWhile(this);
    }

}
