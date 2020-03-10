package net.ssehub.mutator.parsing.ast;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

public class File extends AstElement {
    
    public List<Function> functions = new LinkedList<>();
    
    public File(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        return functions.get(index);
    }
    
    @Override
    public int getNumChildren() {
        return functions.size();
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitFile(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            File other = (File) obj;
            equals = functions.equals(other.functions);
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        return functions.hashCode();
    }
    
}
