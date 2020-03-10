package net.ssehub.mutator.mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.mutation.MutationIdentifier;
import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.Expression;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;
import net.ssehub.mutator.parsing.ast.Statement;
import net.ssehub.mutator.parsing.ast.operations.AstCloner;

public class CopyOverrideExpression extends Mutation {

    public MutationIdentifier sourceIdentifier;
    
    public AstElement toInsert;
    
    public MutationIdentifier target;
    
    public CopyOverrideExpression(MutationIdentifier sourceIdentifier, AstElement toInsert, MutationIdentifier target) {
        this.sourceIdentifier = sourceIdentifier;
        this.toInsert = toInsert;
        this.target = target;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        AstElement targetElem = target.find(ast);
        
        boolean success = false;
        
        if (targetElem != null) {
            String beforeReplacing = getParentStatementText(targetElem);
            
            AstElement toInsertClone = toInsert.accept(new AstCloner(targetElem.parent, true));
            
            ElementReplacer<AstElement> replacer = new ElementReplacer<>();
            success = replacer.replace(targetElem, toInsertClone);
            
            if (success) {
                this.diff = new ArrayList<>(2);
                this.diff.add("-" + beforeReplacing);
                this.diff.add("+" + getParentStatementText(toInsertClone));
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
        return "CopyOverrideExpression(source=" + sourceIdentifier + ", target=" + target + ") -> #" + toInsert.id;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof CopyOverrideExpression) {
            CopyOverrideExpression other = (CopyOverrideExpression) obj;
            equal = sourceIdentifier.equals(other.sourceIdentifier) && target.equals(other.target)
                    && this.toInsert.equals(other.toInsert);
        }
        
        return equal;
    }
    
    public static CopyOverrideExpression find(File file, Random random) {
        ExpressionCollector collector = new ExpressionCollector();
        for (Function func : file.functions) {
            collector.collect(func.body);
        }
        
        Expression mutationSource;
        Expression mutationTarget;
        
        do {
            mutationSource = collector.expressions.get(random.nextInt(collector.expressions.size()));
            mutationTarget = collector.expressions.get(random.nextInt(collector.expressions.size()));
        } while (mutationSource.equals(mutationTarget));
        
        CopyOverrideExpression mutation = new CopyOverrideExpression(
                new MutationIdentifier(mutationSource),
                mutationSource.accept(new AstCloner(null, false)),
                new MutationIdentifier(mutationTarget));
        
        return mutation;
    }
    
}
