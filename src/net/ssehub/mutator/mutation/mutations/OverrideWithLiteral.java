package net.ssehub.mutator.mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.mutation.MutationIdentifier;
import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.Expression;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;
import net.ssehub.mutator.parsing.ast.Literal;
import net.ssehub.mutator.parsing.ast.Statement;
import net.ssehub.mutator.parsing.ast.UnaryExpr;
import net.ssehub.mutator.parsing.ast.UnaryOperator;
import net.ssehub.mutator.parsing.ast.operations.AstCloner;

public class OverrideWithLiteral extends Mutation {

    private MutationIdentifier target;
    
    private Expression literal;
    
    OverrideWithLiteral(MutationIdentifier target, Expression literal) {
        this.target = target;
        this.literal = literal;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        AstElement targetElem = target.find(ast);
        
        boolean success = false;
        
        if (targetElem != null) {
            String beforeReplacing = getParentStatementText(targetElem);

            AstElement literalClone = literal.accept(new AstCloner(targetElem.parent, true));
            
            ElementReplacer<AstElement> replacer = new ElementReplacer<>();
            success = replacer.replace(targetElem, literalClone);
            
            if (success) {
                this.diff = new ArrayList<>(2);
                this.diff.add("-" + beforeReplacing);
                this.diff.add("+" + getParentStatementText(literalClone));
            }
        }
        
        return success;
    }
    
    private static String getParentStatementText(AstElement element) {
        if (element instanceof Statement) {
            return element.getText();
        } else {
            return getParentStatementText(element.parent);
        }
    }

    @Override
    public String toString() {
        return "OverrideWithLiteral(target=" + target + ", literal=" + literal.getText() + ") -> #" + literal.id;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof OverrideWithLiteral) {
            OverrideWithLiteral other = (OverrideWithLiteral) obj;
            equal = target.equals(other.target) && literal.equals(other.literal);
        }
        
        return equal;
    }
    
    @Override
    public int hashCode() {
        return 193 * target.hashCode() + 479 * literal.hashCode();
    }
    
    public static OverrideWithLiteral find(File file, Random random) {
        Collector<Expression> collector = new Collector<>(Expression.class);
        for (Function func : file.functions) {
            collector.collect(func.body);
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
