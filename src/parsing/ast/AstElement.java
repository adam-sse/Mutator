package parsing.ast;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

import org.antlr.v4.runtime.Token;

import parsing.ast.operations.IAstVisitor;

public abstract class AstElement {

    public static final class Location {

        public final int line;
        
        public final int column;
        
        public Location(int line, int column) {
            this.line = line;
            this.column = column;
        }
        
        @Override
        public String toString() {
            return line + ":" + column;
        }
        
        @Override
        public boolean equals(Object obj) {
            boolean equal = false;
            if (obj instanceof Location) {
                Location other = (Location) obj;
                equal = this.line == other.line && this.column == other.column;
            }
            return equal;
        }
        
        @Override
        public int hashCode() {
            return Integer.hashCode(line) + 23 * Integer.hashCode(column);
        }
        
    }
    
    public static boolean PRINT_IDS = true;
    
    private static AtomicLong nextId = new AtomicLong(1);
    
    public AstElement parent;
    
    public Location start;
    
    public Location end;
    
    public long id;
    
    public AstElement(AstElement parent) {
        this.parent = parent;
        this.id = nextId.getAndIncrement();
    }
    
    public void initLocation(Token start, Token end) {
        this.start = new Location(start.getLine(), start.getCharPositionInLine() + 1);
        this.end = new Location(end.getLine(), end.getCharPositionInLine() + 1);
    }
    
    public abstract AstElement getChild(int index) throws IndexOutOfBoundsException;
    
    public abstract int getNumChildren();
    
    /**
     * Pretty-prints this AST.
     * 
     * @param indentation The current indentation level. Use "" (empty string) when calling this for the top-level.
     * 
     * @return This AST pretty-printed into multiple lines. Line separator=\n, indentation=\t.
     */
    public abstract String print(String indentation);
    
    protected String idComment() {
        if (PRINT_IDS) {
            return "/*#" + id + "*/";
        } else {
            return "";
        }
    }
    
    /**
     * Creates a single-line textual representation of this and all child elements.
     * 
     * @return This AST as a single text line.
     */
    public abstract String getText();
    
    public abstract <T> T accept(IAstVisitor<T> visitor);
    
    /**
     * Creates a deep copy of this AST; the copied elements will have new IDs.
     */
    public final AstElement copy(AstElement parent) {
        return copyInternal(this, parent);
    }
    
    private static AstElement copyInternal(AstElement src, AstElement parent) {
        AstElement copy = src.cloneImpl(parent, AstElement::copyInternal);
        copy.start = src.start;
        copy.end = src.end;
        return copy;
    }
    
    /**
     * Creates a deep clone of this AST; the IDs will remain the same.
     */
    public final AstElement clone(AstElement parent) {
        return cloneInternal(this, parent);
    }
    
    private static AstElement cloneInternal(AstElement src, AstElement parent) {
        AstElement copy = src.cloneImpl(parent, AstElement::cloneInternal);
        copy.start = src.start;
        copy.end = src.end;
        copy.id = src.id; // keep the "old" ID since we are a 1-to-1 clone
        return copy;
    }
    
    protected abstract AstElement cloneImpl(AstElement parent, BiFunction<AstElement, AstElement, AstElement> cloneFct);
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(#" + id + ")";
    }
    
    /**
     * Returns if the other given object is an {@link AstElement} that represents equal code
     * (everything equal, except location and parent).
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }
    
}
