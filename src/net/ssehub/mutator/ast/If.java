package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IStatementVisitor;

public class If extends Statement {

    public Expression condition;

    public Statement thenBlock;

    public Statement elseBlock;

    public If(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        switch (index) {
        case 0:
            return this.condition;

        case 1:
            return this.thenBlock;

        case 2:
            if (this.elseBlock != null)
                return this.elseBlock;
            else
                throw new IndexOutOfBoundsException(index);
        default:
            throw new IndexOutOfBoundsException(index);
        }
    }

    @Override
    public int getNumChildren() {
        return this.elseBlock != null ? 3 : 2;
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visitIf(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            If other = (If) obj;
            equals = this.condition.equals(other.condition) && this.thenBlock.equals(other.thenBlock);
            if (this.elseBlock == null) {
                equals &= other.thenBlock == null;
            } else {
                equals &= this.thenBlock.equals(other.thenBlock);
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return 401 * this.condition.hashCode() + 353 * this.thenBlock.hashCode()
                + (this.elseBlock != null ? 373 * this.elseBlock.hashCode() : 13);
    }

}
