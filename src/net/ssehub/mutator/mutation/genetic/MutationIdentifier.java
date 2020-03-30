package net.ssehub.mutator.mutation.genetic;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.operations.IdFinder;

public class MutationIdentifier {

    private long id;

    public MutationIdentifier(AstElement element) {
        this.id = element.id;
    }

    public MutationIdentifier(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "#" + this.id;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj instanceof MutationIdentifier) {
            MutationIdentifier other = (MutationIdentifier) obj;
            equal = this.id == other.id;
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    public AstElement find(AstElement ast) {
        return ast.accept(new IdFinder(this.id));
    }

}
