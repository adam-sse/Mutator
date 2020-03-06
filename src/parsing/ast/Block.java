package parsing.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BiFunction;

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
    public String print(String indentation) {
        StringBuilder sb = new StringBuilder();
        sb.append(indentation).append(idComment()).append("{\n");
        for (Statement st : statements) {
            sb.append(st.print(indentation + "\t"));
        }
        sb.append(indentation).append("}\n");
        return sb.toString();
    }
    
    @Override
    public String getText() {
        StringJoiner sj = new StringJoiner(" ", "{ ", " }");
        for (Statement st : statements) {
            sj.add(st.getText());
        }
        return sj.toString();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visitBlock(this);
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
