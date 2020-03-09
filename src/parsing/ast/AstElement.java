package parsing.ast;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

import org.antlr.v4.runtime.Token;

import parsing.ast.operations.AstLinePrinter;
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
    
    public abstract <T> T accept(IAstVisitor<T> visitor);
    
    /**
     * Shorthand for {@link #accept(IAstVisitor)}ing an {@link AstLinePrinter}.
     */
    public final String getText() {
        return accept(new AstLinePrinter());
    }
    
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
