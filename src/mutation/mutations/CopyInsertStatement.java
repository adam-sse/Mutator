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

    public MutationIdentifier sourceIdentifier;
    
    public Statement toInsert;
    
    public MutationIdentifier reference;
    
    public boolean before;
    
    public CopyInsertStatement(MutationIdentifier sourceIdentifier, Statement toInsert,
            MutationIdentifier reference, boolean before) {
        this.sourceIdentifier = sourceIdentifier;
        this.toInsert = toInsert;
        this.reference = reference;
        this.before = before;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        Statement referenceElem = (Statement) reference.find(ast);
        
        boolean success = false;
        
        if (referenceElem != null && new MutationIdentifier(toInsert).find(ast) == null) {
            StatementInserter inserter = new StatementInserter();
            
            Statement toInsertClone = (Statement) toInsert.accept(new AstCloner(referenceElem.parent, true));
            
            success = inserter.insert(referenceElem, this.before, toInsertClone);

            if (success) {
                this.diff = new ArrayList<>(2);
                if (this.before) {
                    this.diff.add("+" + toInsert.getText());
                    this.diff.add(" " + referenceElem.getText());
                } else {
                    this.diff.add(" " + referenceElem.getText());
                    this.diff.add("+" + toInsert.getText());
                }
            }
        }
        
        return success;
    }

    @Override
    public String toString() {
        return "CopyInsertStatement(source=" + sourceIdentifier + ", reference=" + reference + ", before=" + before
                + ") -> #" + toInsert.id;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof CopyInsertStatement) {
            CopyInsertStatement other = (CopyInsertStatement) obj;
            equal = sourceIdentifier.equals(other.sourceIdentifier) && reference.equals(other.reference)
                    && before == other.before && this.toInsert.equals(other.toInsert);
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
                (Statement) mutationSource.accept(new AstCloner(null, false)),
                new MutationIdentifier(mutationReference), random.nextBoolean());
        
        return mutation;
    }
    
}
