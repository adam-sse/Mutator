package mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import mutation.MutationIdentifier;
import parsing.ast.AstElement;
import parsing.ast.Expression;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.Literal;
import parsing.ast.Statement;
import parsing.ast.UnaryExpr;
import parsing.ast.UnaryOperator;

public class OverrideWithLiteral extends Mutation {

    public MutationIdentifier target;
    
    public String literal;
    
    public boolean negated;
    
    private Long newLitId;
    
    private Long newOpId;
    
    public OverrideWithLiteral(MutationIdentifier target, String literal, boolean negated) {
        this.target = target;
        this.literal = literal;
        this.negated = negated;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        AstElement targetElem = target.find(ast);
        
        boolean success = false;
        
        if (targetElem != null && !isSameLiteral(targetElem) && (newLitId == null || new MutationIdentifier(newLitId).find(ast) == null)) {
            String beforeReplacing = getParentStatementText(targetElem);

            AstElement replaceWith;
            Literal lit = new Literal(targetElem.parent);
            lit.start = targetElem.start;
            lit.end = targetElem.end;
            lit.value = literal;
            if (newLitId == null) {
                newLitId = lit.id;
            } else {
                lit.id = newLitId;
            }
            
            replaceWith = lit;
            
            if (negated) {
                UnaryExpr negation = new UnaryExpr(targetElem.parent);
                negation.start = targetElem.start;
                negation.end = targetElem.end;
                negation.operator = UnaryOperator.MINUS;
                if (newOpId == null) {
                    newOpId = negation.id;
                } else {
                    negation.id = newOpId;
                }
                
                negation.expr = lit;
                lit.parent = negation;
                replaceWith = negation;
            }
            
            
            ElementReplacer<AstElement> replacer = new ElementReplacer<>();
            replacer.replace(targetElem, replaceWith);
            success = replacer.success;
            
            if (success) {
                this.diff = new ArrayList<>(2);
                // find parents
                this.diff.add("-" + beforeReplacing);
                this.diff.add("+" + getParentStatementText(replaceWith));
            }
        }
        
        return success;
    }
    
    private boolean isSameLiteral(AstElement other) {
        boolean same = false;
        if (other instanceof Literal) {
            Literal oLit = (Literal) other;
            same = oLit.value.equals(this.literal);
            if (same && this.negated) {
                // 1 and -1 are not same literals
                same = (oLit.parent instanceof UnaryExpr) && ((UnaryExpr) oLit.parent).operator == UnaryOperator.MINUS;
            }
        }
        return same;
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
        return "OverrideWithLiteral(target=" + target + ", literal=" + (negated ? "-" : "") + literal + ")"
                + (newOpId != null ? " -> #" + newOpId : (newLitId != null ? " -> #" + newLitId : ""));
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof OverrideWithLiteral) {
            OverrideWithLiteral other = (OverrideWithLiteral) obj;
            equal = target.equals(other.target) && literal.equals(other.literal) && negated == other.negated
                    && this.newLitId == other.newLitId && this.newOpId == other.newOpId;
        }
        
        return equal;
    }
    
    public static OverrideWithLiteral find(File file, Random random) {
        ExpressionCollector collector = new ExpressionCollector();
        for (Function func : file.functions) {
            collector.collect(func.body);
        }
        
        Expression mutationTarget = collector.expressions.get(random.nextInt(collector.expressions.size()));
        String literal = String.valueOf(random.nextInt(17));
        
        OverrideWithLiteral mutation = new OverrideWithLiteral(new MutationIdentifier(mutationTarget), literal, random.nextBoolean());
        
        return mutation;
    }
    
}
