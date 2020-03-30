package net.ssehub.mutator.ast;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.ast.operations.IAstVisitor;

public class File extends AstElement {

    public List<AstElement> functions = new LinkedList<>();

    public File(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        return this.functions.get(index);
    }

    @Override
    public int getNumChildren() {
        return this.functions.size();
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitFile(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            File other = (File) obj;
            equals = this.functions.equals(other.functions);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return this.functions.hashCode();
    }

}
