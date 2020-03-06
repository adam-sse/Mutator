package mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import mutation.MutationIdentifier;
import parsing.ast.AstElement;
import parsing.ast.Expression;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.Statement;

public class CopyOverrideExpression extends Mutation {

    public MutationIdentifier source;
    
    public MutationIdentifier target;
    
    private Long newId;
    
    public CopyOverrideExpression(MutationIdentifier source, MutationIdentifier target) {
        this.source = source;
        this.target = target;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        AstElement srcElem = source.find(ast);
        AstElement targetElem = target.find(ast);
        
        boolean success = false;
        
        if (srcElem != null && targetElem != null && (newId == null || new MutationIdentifier(newId).find(ast) == null)) {
            String beforeReplacing = getParentStatementText(targetElem);
            
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
                // find parents
                this.diff.add("-" + beforeReplacing);
                this.diff.add("+" + getParentStatementText(srcCopy));
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
        return "CopyOverrideExpression(source=" + source + ", target=" + target + ")" + (newId != null ? " -> #" + newId : "");
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof CopyOverrideExpression) {
            CopyOverrideExpression other = (CopyOverrideExpression) obj;
            equal = source.equals(other.source) && target.equals(other.target) && this.newId == other.newId;
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
        
        CopyOverrideExpression mutation = new CopyOverrideExpression(new MutationIdentifier(mutationSource), new MutationIdentifier(mutationTarget));
        
        return mutation;
    }
    
}
