package net.ssehub.mutator.mutation.genetic.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.mutation.genetic.MutationIdentifier;

public class CopyInsertStatement extends Mutation {

    private MutationIdentifier sourceIdentifier;

    private Statement toInsert;

    private MutationIdentifier reference;

    private boolean before;

    CopyInsertStatement(MutationIdentifier sourceIdentifier, Statement toInsert, MutationIdentifier reference,
            boolean before) {
        this.sourceIdentifier = sourceIdentifier;
        this.toInsert = toInsert;
        this.reference = reference;
        this.before = before;
    }

    @Override
    public boolean apply(AstElement ast) {
        Statement sourceElem = (Statement) this.sourceIdentifier.find(ast);
        Statement referenceElem = (Statement) this.reference.find(ast);

        boolean success = false;

        if (referenceElem != null && sourceElem != null && new MutationIdentifier(this.toInsert).find(ast) == null
                && sourceElem.equals(this.toInsert)) {
            StatementInserter inserter = new StatementInserter();

            Statement toInsertClone = (Statement) this.toInsert.accept(new AstCloner(referenceElem.parent, true));

            success = inserter.insert(referenceElem, this.before, toInsertClone);

            if (success) {
                this.diff = new ArrayList<>(2);
                if (this.before) {
                    this.diff.add("+" + this.toInsert.getText());
                    this.diff.add(" " + referenceElem.getText());
                } else {
                    this.diff.add(" " + referenceElem.getText());
                    this.diff.add("+" + this.toInsert.getText());
                }
            }
        }

        return success;
    }

    @Override
    public String toString() {
        return "CopyInsertStatement(source=" + this.sourceIdentifier + ", reference=" + this.reference + ", before="
                + this.before + ") -> #" + this.toInsert.id;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj instanceof CopyInsertStatement) {
            CopyInsertStatement other = (CopyInsertStatement) obj;
            equal = this.sourceIdentifier.equals(other.sourceIdentifier) && this.reference.equals(other.reference)
                    && this.before == other.before;
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return this.sourceIdentifier.hashCode() + 137 * this.reference.hashCode() + 211 * Boolean.hashCode(this.before);
    }

    public static CopyInsertStatement find(File file, Random random) {
        Collector<Statement> collector = new Collector<>(Statement.class);
        for (AstElement func : file.functions) {
            if (func instanceof Function) {
                collector.collect(((Function) func).body);
            }
        }

        Statement mutationSource;
        Statement mutationReference;

        do {
            mutationSource = collector.getFoundElements().get(random.nextInt(collector.getFoundElements().size()));
            mutationReference = collector.getFoundElements().get(random.nextInt(collector.getFoundElements().size()));
        } while (mutationSource.id == mutationReference.id || mutationReference.parent instanceof Function);

        CopyInsertStatement mutation = new CopyInsertStatement(new MutationIdentifier(mutationSource),
                (Statement) mutationSource.accept(new AstCloner(null, false)),
                new MutationIdentifier(mutationReference), random.nextBoolean());

        return mutation;
    }

}
