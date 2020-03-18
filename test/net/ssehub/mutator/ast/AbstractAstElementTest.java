package net.ssehub.mutator.ast;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.ssehub.mutator.ast.operations.AstPrettyPrinter;

public abstract class AbstractAstElementTest {

    protected abstract AstElement[] createElements();
    
    protected abstract boolean equal(int element1, int elemen2);
    
    protected abstract String getLineString(int element);
    
    protected abstract String getPrettyString(int element);
    
    @Test
    public void testEquals() {
        AstElement[] elements = createElements();
        
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < elements.length; j++) {
                boolean expected = equal(i, j);
                assertThat(i + " should" + (expected ? "" : " not") + " equal "  + j,
                        elements[i].equals(elements[j]), is(expected));
            }
        }
    }
    
    @Test
    public void testHashCode() {
        AstElement[] elements = createElements();
        
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < elements.length; j++) {
                boolean expected = equal(i, j);
                
                assertThat(i + " should" + (expected ? "" : " not") + " equal "  + j,
                        elements[i].hashCode() == elements[j].hashCode(), is(expected));
            }
        }
    }
    
    @Test
    public void testLineString() {
        AstElement[] elements = createElements();
        
        for (int i = 0; i < elements.length; i++) {
            assertThat(elements[i].getText(), is(getLineString(i)));
        }
    }
    
    @Test
    public void testPrettyString() {
        AstElement[] elements = createElements();
        
        AstPrettyPrinter printer = new AstPrettyPrinter(false);
        for (int i = 0; i < elements.length; i++) {
            assertThat(elements[i].accept(printer), is(getPrettyString(i)));
        }
    }
    
}
