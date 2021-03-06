package net.ssehub.mutator.mutation.genetic.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.mutation.genetic.MutationIdentifier;

public class DeleteStatement extends Mutation {

    private MutationIdentifier target;

    DeleteStatement(MutationIdentifier target) {
        this.target = target;
    }

    @Override
    public boolean apply(AstElement ast) {
        Statement targetElem = (Statement) this.target.find(ast);

        boolean success = false;

        if (targetElem != null) {
            StatementDeleter deleter = new StatementDeleter();
            success = deleter.delete(targetElem);

            if (success) {
                this.diff = new ArrayList<>(1);
                this.diff.add("-" + targetElem.getText());
            }
        }

        return success;
    }

    @Override
    public String toString() {
        return "DeleteStatement(target=" + this.target + ")";
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj instanceof DeleteStatement) {
            DeleteStatement other = (DeleteStatement) obj;
            equal = this.target.equals(other.target);
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return this.target.hashCode();
    }

    public static DeleteStatement find(File file, Random random) {
        Collector<Statement> collector = new Collector<>(Statement.class);
        for (AstElement func : file.functions) {
            if (func instanceof Function) {
                collector.collect(((Function) func).body);
                collector.getFoundElements().remove(((Function) func).body);
            }
        }

        Statement mutationTarget = collector.getFoundElements()
                .get(random.nextInt(collector.getFoundElements().size()));

        DeleteStatement mutation = new DeleteStatement(new MutationIdentifier(mutationTarget));

        return mutation;
    }

}
