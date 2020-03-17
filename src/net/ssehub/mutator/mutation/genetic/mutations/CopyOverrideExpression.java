package net.ssehub.mutator.mutation.genetic.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.mutation.genetic.MutationIdentifier;

public class CopyOverrideExpression extends Mutation {

    private MutationIdentifier sourceIdentifier;
    
    private AstElement toInsert;
    
    private MutationIdentifier target;
    
    CopyOverrideExpression(MutationIdentifier sourceIdentifier, AstElement toInsert, MutationIdentifier target) {
        this.sourceIdentifier = sourceIdentifier;
        this.toInsert = toInsert;
        this.target = target;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        AstElement sourceElem = sourceIdentifier.find(ast);
        AstElement targetElem = target.find(ast);
        
        boolean success = false;
        
        if (targetElem != null && sourceElem != null && sourceElem.equals(toInsert)) {
            String beforeReplacing = Util.getParentStatementText(targetElem);
            
            AstElement toInsertClone = toInsert.accept(new AstCloner(targetElem.parent, true));
            
            ElementReplacer<AstElement> replacer = new ElementReplacer<>();
            success = replacer.replace(targetElem, toInsertClone);
            
            if (success) {
                this.diff = new ArrayList<>(2);
                this.diff.add("-" + beforeReplacing);
                this.diff.add("+" + Util.getParentStatementText(toInsertClone));
            }
        }
        
        return success;
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
            equal = sourceIdentifier.equals(other.sourceIdentifier) && target.equals(other.target);
        }
        
        return equal;
    }
    
    @Override
    public int hashCode() {
        return sourceIdentifier.hashCode() + 233 * target.hashCode();
    }
    
    public static CopyOverrideExpression find(File file, Random random) {
        Collector<Expression> collector = new Collector<>(Expression.class);
        for (Function func : file.functions) {
            collector.collect(func.body);
        }
        
        Expression mutationSource;
        Expression mutationTarget;
        
        do {
            mutationSource = collector.getFoundElements().get(random.nextInt(collector.getFoundElements().size()));
            mutationTarget = collector.getFoundElements().get(random.nextInt(collector.getFoundElements().size()));
        } while (mutationSource.id == mutationTarget.id || mutationSource.equals(mutationTarget));
        
        CopyOverrideExpression mutation = new CopyOverrideExpression(
                new MutationIdentifier(mutationSource),
                mutationSource.accept(new AstCloner(null, false)),
                new MutationIdentifier(mutationTarget));
        
        return mutation;
    }
    
}
