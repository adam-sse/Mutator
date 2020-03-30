package net.ssehub.mutator.ast;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.ast.operations.IStatementVisitor;

public class Block extends Statement {

    public List<Statement> statements = new LinkedList<>();

    public Block(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        return this.statements.get(index);
    }

    @Override
    public int getNumChildren() {
        return this.statements.size();
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visitBlock(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Block other = (Block) obj;
            equals = this.statements.equals(other.statements);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return this.statements.hashCode();
    }

}
