package net.ssehub.mutator.mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.mutation.MutationIdentifier;
import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;
import net.ssehub.mutator.parsing.ast.Statement;
import net.ssehub.mutator.parsing.ast.operations.AstCloner;

public class CopyOverrideStatement extends Mutation {

    private MutationIdentifier sourceIdentifier;
    
    private AstElement toInsert;
    
    private MutationIdentifier target;
    
    public CopyOverrideStatement(MutationIdentifier sourceIdentifier, AstElement toInsert, MutationIdentifier target) {
        this.sourceIdentifier = sourceIdentifier;
        this.toInsert = toInsert;
        this.target = target;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        AstElement targetElem = target.find(ast);
        
        boolean success = false;
        
        if (targetElem != null) {
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
            equal = sourceIdentifier.equals(other.sourceIdentifier) && target.equals(other.target)
                    && this.toInsert.equals(other.toInsert);
        }
        
        return equal;
    }
    
    @Override
    public int hashCode() {
        return sourceIdentifier.hashCode() + 401 * target.hashCode() + 181 * toInsert.hashCode();
    }
    
    public static CopyOverrideStatement find(File file, Random random) {
        StatementCollector collector = new StatementCollector();
        for (Function func : file.functions) {
            collector.collect(func.body);
            collector.getStatements().remove(func.body);
        }
        
        Statement mutationSource;
        Statement mutationTarget;
        
        do {
            mutationSource = collector.getStatements().get(random.nextInt(collector.getStatements().size()));
            mutationTarget = collector.getStatements().get(random.nextInt(collector.getStatements().size()));
        } while (mutationSource.equals(mutationTarget));
        
        CopyOverrideStatement mutation = new CopyOverrideStatement(
                new MutationIdentifier(mutationSource),
                mutationSource.accept(new AstCloner(null, false)),
                new MutationIdentifier(mutationTarget));
        
        return mutation;
    }
    
}
