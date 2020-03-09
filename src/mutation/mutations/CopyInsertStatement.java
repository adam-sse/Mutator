package mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import mutation.MutationIdentifier;
import parsing.ast.AstElement;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.Statement;
import parsing.ast.operations.AstCloner;

public class CopyInsertStatement extends Mutation {

    public MutationIdentifier source;
    
    public MutationIdentifier reference;
    
    public boolean before;
    
    private Long newId;
    
    public CopyInsertStatement(MutationIdentifier source, MutationIdentifier reference, boolean before) {
        this.source = source;
        this.reference = reference;
        this.before = before;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        Statement srcElem = (Statement) source.find(ast);
        Statement targetElem = (Statement) reference.find(ast);
        
        boolean success = false;
        
        if (srcElem != null && targetElem != null && (newId == null || new MutationIdentifier(newId).find(ast) == null)) {
            StatementInserter inserter = new StatementInserter();
            
            Statement srcCopy = (Statement) srcElem.accept(new AstCloner(targetElem.parent, false));
            if (newId == null) {
                newId = srcCopy.id;
            } else {
                srcCopy.id = newId;
            }
            
            success = inserter.insert(targetElem, this.before, srcCopy);

            if (success) {
                this.diff = new ArrayList<>(2);
                if (this.before) {
                    this.diff.add("+" + srcCopy.getText());
                    this.diff.add(" " + targetElem.getText());
                } else {
                    this.diff.add(" " + targetElem.getText());
                    this.diff.add("+" + srcCopy.getText());
                }
            }
        }
        
        return success;
    }

    @Override
    public String toString() {
        return "CopyInsertStatement(source=" + source + ", reference=" + reference+ ", before=" + before + ")" + (newId != null ? " -> #" + newId : "");
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof CopyInsertStatement) {
            CopyInsertStatement other = (CopyInsertStatement) obj;
            equal = source.equals(other.source) && reference.equals(other.reference) && before == other.before && this.newId == other.newId;
        }
        
        return equal;
    }
    
    public static CopyInsertStatement find(File file, Random random) {
        StatementCollector collector = new StatementCollector();
        for (Function func : file.functions) {
            collector.collect(func.body);
            collector.statements.remove(func.body);
        }
        
        Statement mutationSource;
        Statement mutationReference;
        
        do {
            mutationSource = collector.statements.get(random.nextInt(collector.statements.size()));
            mutationReference = collector.statements.get(random.nextInt(collector.statements.size()));
        } while (mutationSource.equals(mutationReference));
        
        CopyInsertStatement mutation = new CopyInsertStatement(new MutationIdentifier(mutationSource),
                new MutationIdentifier(mutationReference), random.nextBoolean());
        
        return mutation;
    }
    
}
