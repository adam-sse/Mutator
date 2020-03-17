package net.ssehub.mutator.mutation.genetic.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.mutation.genetic.MutationIdentifier;

public class CopyOverrideStatement extends Mutation {

    private MutationIdentifier sourceIdentifier;
    
    private AstElement toInsert;
    
    private MutationIdentifier target;
    
    CopyOverrideStatement(MutationIdentifier sourceIdentifier, AstElement toInsert, MutationIdentifier target) {
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
            AstElement toInsertClone = toInsert.accept(new AstCloner(targetElem.parent, true));
            
            ElementReplacer<AstElement> replacer = new ElementReplacer<>();
            success = replacer.replace(targetElem, toInsertClone);

            if (success) {
                this.diff = new ArrayList<>(2);
                this.diff.add("-" + targetElem.getText());
                this.diff.add("+" + toInsertClone.getText());
            }
        }
        
        return success;
    }

    @Override
    public String toString() {
        return "CopyOverrideStatement(source=" + sourceIdentifier + ", target=" + target + ") -> #" + toInsert.id;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof CopyOverrideStatement) {
            CopyOverrideStatement other = (CopyOverrideStatement) obj;
            equal = sourceIdentifier.equals(other.sourceIdentifier) && target.equals(other.target);
        }
        
        return equal;
    }
    
    @Override
    public int hashCode() {
        return sourceIdentifier.hashCode() + 401 * target.hashCode();
    }
    
    public static CopyOverrideStatement find(File file, Random random) {
        Collector<Statement> collector = new Collector<>(Statement.class);
        for (Function func : file.functions) {
            collector.collect(func.body);
        }
        
        Statement mutationSource;
        Statement mutationTarget;
        
        do {
            mutationSource = collector.getFoundElements().get(random.nextInt(collector.getFoundElements().size()));
            mutationTarget = collector.getFoundElements().get(random.nextInt(collector.getFoundElements().size()));
        } while (mutationSource.id == mutationTarget.id || mutationTarget.parent instanceof Function
                || mutationSource.equals(mutationTarget));
        
        CopyOverrideStatement mutation = new CopyOverrideStatement(
                new MutationIdentifier(mutationSource),
                mutationSource.accept(new AstCloner(null, false)),
                new MutationIdentifier(mutationTarget));
        
        return mutation;
    }
    
}
