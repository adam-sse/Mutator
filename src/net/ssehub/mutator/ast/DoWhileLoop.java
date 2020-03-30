package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IStatementVisitor;

public class DoWhileLoop extends Loop {

    public DoWhileLoop(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0:
            return this.body;

        case 1:
            return this.condition;

        default:
            throw new IndexOutOfBoundsException(index);
        }
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visitDoWhileLoop(this);
    }

}
