package parsing.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

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
    public String print(String indentation) {
        StringBuilder sb = new StringBuilder();
        for (Function func : functions) {
            sb.append(func.print(indentation));
        }
        return sb.toString();
    }
    
    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (Function func : functions) {
            sb.append(func.getText()).append(" ");
        }
        return sb.toString();
    }
    
    @Override
    public void accept(IAstVisitor visitor) {
        visitor.visitFile(this);
    }

    @Override
    public File cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct) {
        File clone = new File(parent);
        
        for (Function f : functions) {
            clone.functions.add((Function) cloneFct.apply(f, clone));
        }
        
        return clone;
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
    
}
