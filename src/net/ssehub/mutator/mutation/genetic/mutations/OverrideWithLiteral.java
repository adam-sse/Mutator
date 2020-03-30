package net.ssehub.mutator.mutation.genetic.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.UnaryOperator;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.mutation.genetic.MutationIdentifier;

public class OverrideWithLiteral extends Mutation {

    private MutationIdentifier target;

    private Expression literal;

    OverrideWithLiteral(MutationIdentifier target, Expression literal) {
        this.target = target;
        this.literal = literal;
    }

    @Override
    public boolean apply(AstElement ast) {
        AstElement targetElem = this.target.find(ast);

        boolean success = false;

        if (targetElem != null) {
            String beforeReplacing = Util.getParentStatementText(targetElem);

            AstElement literalClone = this.literal.accept(new AstCloner(targetElem.parent, true));

            ElementReplacer<AstElement> replacer = new ElementReplacer<>();
            success = replacer.replace(targetElem, literalClone);

            if (success) {
                this.diff = new ArrayList<>(2);
                this.diff.add("-" + beforeReplacing);
                this.diff.add("+" + Util.getParentStatementText(literalClone));
            }
        }

        return success;
    }

    @Override
    public String toString() {
        return "OverrideWithLiteral(target=" + this.target + ", literal=" + this.literal.getText() + ") -> #"
                + this.literal.id;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj instanceof OverrideWithLiteral) {
            OverrideWithLiteral other = (OverrideWithLiteral) obj;
            equal = this.target.equals(other.target) && this.literal.equals(other.literal);
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return 193 * this.target.hashCode() + 479 * this.literal.hashCode();
    }

    public static OverrideWithLiteral find(File file, Random random) {
        Collector<Expression> collector = new Collector<>(Expression.class);
        for (AstElement func : file.functions) {
            if (func instanceof Function) {
                collector.collect(((Function) func).body);
            }
        }

        Expression literal;
        Expression mutationTarget;

        do {
            mutationTarget = collector.getFoundElements().get(random.nextInt(collector.getFoundElements().size()));

            String literalStr = String.valueOf(random.nextInt(17));
            boolean negated = random.nextBoolean();

            Literal lit = new Literal(null);
            lit.start = mutationTarget.start;
            lit.end = mutationTarget.end;
            lit.value = literalStr;

            literal = lit;

            if (negated) {
                UnaryExpr negation = new UnaryExpr(null);
                negation.start = mutationTarget.start;
                negation.end = mutationTarget.end;
                negation.operator = UnaryOperator.MINUS;

                negation.expr = lit;
                lit.parent = negation;
                literal = negation;
            }
        } while (mutationTarget.equals(literal));

        OverrideWithLiteral mutation = new OverrideWithLiteral(new MutationIdentifier(mutationTarget), literal);

        return mutation;
    }

}
