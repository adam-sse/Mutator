package net.ssehub.mutator.ast;

import net.ssehub.mutator.ast.operations.IStatementVisitor;

public class JumpStmt extends Statement {

    public enum Type {

        CONTINUE("continue"),

        BREAK("break");

        private String str;

        private Type(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return this.str;
        }

    }

    public Type type;

    public JumpStmt(AstElement parent) {
        super(parent);
    }

    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException(index);
    }

    @Override
    public int getNumChildren() {
        return 0;
    }

    @Override
    public <T> T accept(IStatementVisitor<T> visitor) {
        return visitor.visitJumpStmt(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            JumpStmt other = (JumpStmt) obj;
            equals = this.type.equals(other.type);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return 149 * this.type.hashCode();
    }

}
