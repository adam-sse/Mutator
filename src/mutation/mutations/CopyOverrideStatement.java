package mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import mutation.MutationIdentifier;
import parsing.ast.AstElement;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.Statement;

public class CopyOverrideStatement extends Mutation {

    public MutationIdentifier source;
    
    public MutationIdentifier target;
    
    private Long newId;
    
    public CopyOverrideStatement(MutationIdentifier source, MutationIdentifier target) {
        this.source = source;
        this.target = target;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        AstElement srcElem = source.find(ast);
        AstElement targetElem = target.find(ast);
        
        boolean success = false;
        
        if (srcElem != null && targetElem != null && (newId == null || new MutationIdentifier(newId).find(ast) == null)) {
            ElementReplacer<AstElement> replacer = new ElementReplacer<>();
            AstElement srcCopy = srcElem.copy(targetElem.parent);
            if (newId == null) {
                newId = srcCopy.id;
            } else {
                srcCopy.id = newId;
            }
            
            replacer.replace(targetElem, srcCopy);
            success = replacer.success;

            if (success) {
                this.diff = new ArrayList<>(2);
                this.diff.add("-" + targetElem.getText());
                this.diff.add("+" + srcCopy.getText());
            }
        }
        
        return success;
    }

    @Override
    public String toString() {
        return "CopyOverrideStatement(source=" + source + ", target=" + target + ")" + (newId != null ? " -> #" + newId : "");
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof CopyOverrideStatement) {
            CopyOverrideStatement other = (CopyOverrideStatement) obj;
            equal = source.equals(other.source) && target.equals(other.target) && this.newId == other.newId;
        }
        
        return equal;
    }
    
    public static CopyOverrideStatement find(File file, Random random) {
        StatementCollector collector = new StatementCollector();
        for (Function func : file.functions) {
            collector.collect(func.body);
            collector.statements.remove(func.body);
        }
        
        Statement mutationSource;
        Statement mutationTarget;
        
        do {
            mutationSource = collector.statements.get(random.nextInt(collector.statements.size()));
            mutationTarget = collector.statements.get(random.nextInt(collector.statements.size()));
        } while (mutationSource.equals(mutationTarget));
        
        CopyOverrideStatement mutation = new CopyOverrideStatement(new MutationIdentifier(mutationSource), new MutationIdentifier(mutationTarget));
        
        return mutation;
    }
    
}
