package mutation.mutations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import mutation.MutationIdentifier;
import parsing.ast.AstElement.Location;
import parsing.ast.Block;
import parsing.ast.EmptyStmt;
import parsing.ast.Statement;

public class DeleteStatementTest {

    @Test
    public void testEquals() {
        MutationIdentifier id1 = new MutationIdentifier(123);
        DeleteStatement mut1 = new DeleteStatement(id1);
        
        assertThat(mut1.equals(mut1), is(true));

        MutationIdentifier id2 = new MutationIdentifier(321);
        DeleteStatement mut2 = new DeleteStatement(id2);
        assertThat(mut1.equals(mut2), is(false));

        MutationIdentifier id4 = new MutationIdentifier(123);
        DeleteStatement mut4 = new DeleteStatement(id4);
        assertThat(mut1.equals(mut4), is(true));
        
        DeleteStatement mut5 = new DeleteStatement(id1);
        assertThat(mut1.equals(mut5), is(true));
    }
    
    @Test
    public void testDoubleApply() {
        Block parent = new Block(null);
        
        Statement st1 = new EmptyStmt(parent);
        st1.start = new Location(1, 1);
        st1.end = new Location(1, 30);
        parent.statements.add(st1);
        
        Statement st2 = new EmptyStmt(parent);
        st2.start = new Location(2, 1);
        st2.end = new Location(2, 30);
        parent.statements.add(st2);
        
        Statement st3 = new EmptyStmt(parent);
        st3.start = new Location(3, 1);
        st3.end = new Location(3, 30);
        parent.statements.add(st3);
        
        DeleteStatement mut = new DeleteStatement(new MutationIdentifier(st1));
        
        boolean applied = mut.apply(parent);
        assertThat(applied, is(true));
        assertThat(parent.statements, is(Arrays.asList(st1, st2)));
        
        applied = mut.apply(parent);
        assertThat(applied, is(false));
        assertThat(parent.statements, is(Arrays.asList(st1, st2)));
    }
    
}
