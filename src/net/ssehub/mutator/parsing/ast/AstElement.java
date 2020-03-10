package net.ssehub.mutator.parsing.ast;

import java.util.concurrent.atomic.AtomicLong;

import org.antlr.v4.runtime.Token;

import net.ssehub.mutator.parsing.ast.operations.AstLinePrinter;
import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

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
