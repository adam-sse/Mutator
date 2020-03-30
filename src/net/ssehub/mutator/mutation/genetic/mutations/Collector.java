package net.ssehub.mutator.mutation.genetic.mutations;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.operations.FullVisitor;
import net.ssehub.mutator.ast.operations.SingleOperationVisitor;

class Collector<T> extends SingleOperationVisitor<Void> {

    private Class<T> type;

    private List<T> foundElements = new LinkedList<>();

    public Collector(Class<T> type) {
        this.type = type;
    }

    public List<T> getFoundElements() {
        return this.foundElements;
    }

    public void collect(AstElement element) {
        FullVisitor visitor = new FullVisitor(this);
        element.accept(visitor);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Void visit(AstElement element) {
        if (this.type.isAssignableFrom(element.getClass())) {
            this.foundElements.add((T) element);
        }
        return null;
    }

}
