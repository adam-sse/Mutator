package net.ssehub.mutator.ast;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.ast.operations.IAstVisitor;

public class FunctionDecl extends AstElement {

    public Type type;

    public String name;

    public List<Declaration> parameters = new LinkedList<>();

    public FunctionDecl(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        if (index == 0)
            return this.type;
        else if (index >= 1 && index - 1 < this.parameters.size())
            return this.parameters.get(index - 1);
        else
            throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int getNumChildren() {
        return 1 + this.parameters.size();
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitFunctionDecl(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            FunctionDecl other = (FunctionDecl) obj;
            equals = this.type.equals(other.type) && this.name.equals(other.name)
                    && this.parameters.equals(other.parameters);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + 487 * this.type.hashCode() + 271 * this.parameters.hashCode();
    }

}
