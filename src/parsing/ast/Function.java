package parsing.ast;

import java.util.LinkedList;
import java.util.List;

import parsing.ast.operations.IAstVisitor;

public class Function extends AstElement {

    public Type type;
    
    public String name;
    
    public List<Declaration> parameters = new LinkedList<>();
    
    public Block body;
    
    public Function(AstElement parent) {
        super(parent);
    }
    
    @Override
    public AstElement getChild(int index) throws IndexOutOfBoundsException {
        if (index == 0) {
            return type;
        } else if (index >= 1 && index - 1 < parameters.size()) {
            return parameters.get(index - 1);
        } else if (index == 1 + parameters.size()) {
            return body;
        } else {
            throw new IndexOutOfBoundsException(index);
        }
    }
    
    @Override
    public int getNumChildren() {
        return 2 + parameters.size();
    }
    
    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visitFunction(this);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (super.equals(obj)) {
            Function other = (Function) obj;
            equals = type.equals(other.type) && name.equals(other.name) && parameters.equals(other.parameters)
                    && body.equals(other.body);
        }
        return equals;
    }
    
}
