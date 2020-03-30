package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IAstVisitor;

public class Function extends AstElement {

    public FunctionDecl header;

    public Block body;

    public Function(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        if (index == 0)
            return this.header;
        else if (index == 1)
            return this.body;
        else
            throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int getNumChildren() {
        return 2;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitFunction(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Function other = (Function) obj;
            equals = this.header.equals(other.header) && this.body.equals(other.body);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return 389 * this.header.hashCode() + 541 * this.body.hashCode();
    }

}
