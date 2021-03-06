package net.ssehub.mutator.mutation.genetic.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.mutation.genetic.MutationIdentifier;

public class SwapOperands extends Mutation {

    private MutationIdentifier targetIdentifier;

    private long leftId;

    private long rightId;

    SwapOperands(MutationIdentifier target, long leftId, long rightId) {
        this.targetIdentifier = target;
        this.leftId = leftId;
        this.rightId = rightId;
    }

    @Override
    public boolean apply(AstElement ast) {
        BinaryExpr targetElem = (BinaryExpr) this.targetIdentifier.find(ast);

        boolean applied = false;

        if (targetElem != null && this.leftId == targetElem.left.id && this.rightId == targetElem.right.id) {

            String before = Util.getParentStatementText(targetElem);

            Expression left = targetElem.left;
            Expression right = targetElem.right;

            targetElem.left = right;
            targetElem.right = left;

            applied = true;
            this.diff = new ArrayList<>(2);
            this.diff.add("-" + before);
            this.diff.add("+" + Util.getParentStatementText(targetElem));
        }

        return applied;
    }

    @Override
    public String toString() {
        return "SwapOperands(target=" + this.targetIdentifier + ")";
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj instanceof SwapOperands) {
            SwapOperands other = (SwapOperands) obj;
            equal = this.targetIdentifier.equals(other.targetIdentifier) && this.leftId == other.leftId
                    && this.rightId == other.rightId;
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return 17 * this.targetIdentifier.hashCode() + 281 * Long.hashCode(this.leftId)
                + 29 * Long.hashCode(this.rightId);
    }

    public static SwapOperands find(File file, Random random) {
        Collector<BinaryExpr> collector = new Collector<>(BinaryExpr.class);
        for (AstElement func : file.functions) {
            if (func instanceof Function) {
                collector.collect(((Function) func).body);
            }
        }

        SwapOperands result = null;

        if (collector.getFoundElements().size() > 0) {
            BinaryExpr mutationTarget = collector.getFoundElements()
                    .get(random.nextInt(collector.getFoundElements().size()));

            result = new SwapOperands(new MutationIdentifier(mutationTarget), mutationTarget.left.id,
                    mutationTarget.right.id);
        }

        return result;
    }

}
