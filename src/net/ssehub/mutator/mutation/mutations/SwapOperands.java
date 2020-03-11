package net.ssehub.mutator.mutation.mutations;

import java.util.Random;

import net.ssehub.mutator.mutation.MutationIdentifier;
import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.BinaryExpr;
import net.ssehub.mutator.parsing.ast.Expression;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;

public class SwapOperands extends Mutation {

    private MutationIdentifier targetIdentifier;
    
    private long leftId;
    
    private long rightId;
    
    SwapOperands(MutationIdentifier target, long leftId, long rightId) {
        this.targetIdentifier = target;
        this.leftId = leftId;
        this.rightId = rightId;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        BinaryExpr targetElem = (BinaryExpr) targetIdentifier.find(ast);
        
        boolean applied = false;
        
        if (targetElem != null && leftId == targetElem.left.id && rightId == targetElem.right.id) {
            
            Expression left = targetElem.left;
            Expression right = targetElem.right;
            
            targetElem.left = right;
            targetElem.right = left;
            
            applied = true;
        }
        
        return applied;
    }

    @Override
    public String toString() {
        return "SwapOperands(target=" + targetIdentifier + ")";
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof SwapOperands) {
            SwapOperands other = (SwapOperands) obj;
            equal = targetIdentifier.equals(other.targetIdentifier)
                    && leftId == other.leftId && rightId == other.rightId;
        }
        
        return equal;
    }
    
    @Override
    public int hashCode() {
        return 17 * this.targetIdentifier.hashCode() + 281 * Long.hashCode(leftId) + 29 * Long.hashCode(rightId);
    }


    public static SwapOperands find(File file, Random random) {
        Collector<BinaryExpr> collector = new Collector<>(BinaryExpr.class);
        for (Function func : file.functions) {
            collector.collect(func.body);
        }

        SwapOperands result = null;
        
        if (collector.getFoundElements().size() > 0) {
            BinaryExpr mutationTarget = collector.getFoundElements().get(
                    random.nextInt(collector.getFoundElements().size()));
            
            result = new SwapOperands(new MutationIdentifier(mutationTarget),
                    mutationTarget.left.id, mutationTarget.right.id);
            
        }
        
        return result;
    }
    
}
