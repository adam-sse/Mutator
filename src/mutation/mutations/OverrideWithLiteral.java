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
import parsing.ast.operations.AstCloner;

public class OverrideWithLiteral extends Mutation {

    public MutationIdentifier target;
    
    public Expression literal;
    
    public OverrideWithLiteral(MutationIdentifier target, Expression literal) {
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
            equal = target.equals(other.target) && literal.equals(other.literal) && this.literal.equals(other.literal);
        }
        
        return equal;
    }
    
    public static OverrideWithLiteral find(File file, Random random) {
        ExpressionCollector collector = new ExpressionCollector();
        for (Function func : file.functions) {
            collector.collect(func.body);
        }
        
        Expression literal;
        Expression mutationTarget;
        
        do {
            mutationTarget = collector.expressions.get(random.nextInt(collector.expressions.size()));
            
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
