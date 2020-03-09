package parsing.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import parsing.ast.operations.IAstVisitor;

public class Block extends Statement {

    public List<Statement> statements = new LinkedList<>();
    
    public Block(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        return statements.get(index);
    }
    
    @Override
    public int getNumChildren() {
        return statements.size();
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitBlock(this);
    }

    @Override
    public Block cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        Block clone = new Block(parent);
        
        for (Statement stmt : statements) {
            clone.statements.add((Statement) cloneFct.apply(stmt, clone));
        }
        
        return clone;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Block other = (Block) obj;
            equals = statements.equals(other.statements);
        }
        return equals;
    }

}
