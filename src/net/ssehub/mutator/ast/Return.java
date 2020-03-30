package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IStatementVisitor;

public class Return extends Statement {

    public Expression value;

    public Return(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        if (index == 0 && this.value != null)
            return this.value;
        else
            throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int getNumChildren() {
        return this.value != null ? 1 : 0;
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visitReturn(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Return other = (Return) obj;
            if (this.value != null) {
                equals = this.value.equals(other.value);
            } else {
                equals = other.value == null;
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return 509 + (this.value != null ? 457 * this.value.hashCode() : 523);
    }

}
