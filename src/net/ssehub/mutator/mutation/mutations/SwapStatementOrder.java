package net.ssehub.mutator.mutation.mutations;

import java.util.ArrayList;
import java.util.Random;

import net.ssehub.mutator.mutation.MutationIdentifier;
import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.Block;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;
import net.ssehub.mutator.parsing.ast.Statement;

public class SwapStatementOrder extends Mutation {

    private MutationIdentifier blockIdentifier;
    
    private int index;
    
    private long e1Id;
    
    private long e2Id;
    
    SwapStatementOrder(MutationIdentifier blockIdentifier, int index, long e1Id, long e2Id) {
        this.blockIdentifier = blockIdentifier;
        this.index = index;
        this.e1Id = e1Id;
        this.e2Id = e2Id;
    }
    
    @Override
    public boolean apply(AstElement ast) {
        Block block = (Block) blockIdentifier.find(ast);
        
        boolean applied = false;
        
        if (block != null && block.statements.size() > index + 1
                && e1Id == block.statements.get(index).id && e2Id == block.statements.get(index + 1).id) {
            
            
            Statement e1 = block.statements.get(index);
            Statement e2 = block.statements.get(index + 1);
            
            block.statements.set(index, e2);
            block.statements.set(index + 1, e1);
            
            applied = true;
            this.diff = new ArrayList<>(2);
            this.diff.add("-" + e1.getText());
            this.diff.add("-" + e2.getText());
            this.diff.add("+" + e2.getText());
            this.diff.add("+" + e1.getText());
        }
        
        return applied;
    }

    @Override
    public String toString() {
        return "SwapStatementOrder(block=" + blockIdentifier + ", index=" + index + ")";
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        
        if (obj instanceof SwapStatementOrder) {
            SwapStatementOrder other = (SwapStatementOrder) obj;
            equal = blockIdentifier.equals(other.blockIdentifier) && index == other.index
                    && e1Id == other.e1Id && e2Id == other.e2Id;
        }
        
        return equal;
    }
    
    @Override
    public int hashCode() {
        return 17 * this.blockIdentifier.hashCode() + 281 * Long.hashCode(e1Id) + 29 * Long.hashCode(e2Id)
                + Integer.hashCode(index);
    }


    public static SwapStatementOrder find(File file, Random random) {
        Collector<Block> collector = new Collector<>(Block.class);
        for (Function func : file.functions) {
            collector.collect(func.body);
        }

        SwapStatementOrder result = null;
        
        if (collector.getFoundElements().size() > 0) {
            Block block = collector.getFoundElements().get(
                    random.nextInt(collector.getFoundElements().size()));
            
            if (block.statements.size() >= 2) {
                
                int index = random.nextInt(block.statements.size() - 1);
                
                result = new SwapStatementOrder(new MutationIdentifier(block), index,
                        block.statements.get(index).id, block.statements.get(index + 1).id);
                
            }
            
        }
        
        return result;
    }
    
}
