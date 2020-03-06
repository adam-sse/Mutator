package mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import mutation.MutationIdentifier;
import parsing.ast.AstElement;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.Statement;

public class DeleteStatement extends Mutation {

    public MutationIdentifier target;
    
    public DeleteStatement(MutationIdentifier target) {
        this.target = target;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        Statement targetElem = (Statement) target.find(ast);
        
        boolean success = false;
        
        if (targetElem != null) {
            StatementDeleter deleter = new StatementDeleter();
            deleter.delete(targetElem);
            success = deleter.success;
            
            if (success) {
                this.diff = new ArrayList<>(1);
                this.diff.add("-" + targetElem.getText());
            }
        }
        
        return success;
    }

    @Override
    public String toString() {
        return "DeleteStatement(target=" + target + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof DeleteStatement) {
            DeleteStatement other = (DeleteStatement) obj;
            equal = target.equals(other.target);
        }
        
        return equal;
    }

    public static DeleteStatement find(File file, Random random) {
        StatementCollector collector = new StatementCollector();
        for (Function func : file.functions) {
            collector.collect(func.body);
            collector.statements.remove(func.body);
        }
        
        Statement mutationTarget = collector.statements.get(random.nextInt(collector.statements.size()));
        
        DeleteStatement mutation = new DeleteStatement(new MutationIdentifier(mutationTarget));
        
        return mutation;
    }

}
