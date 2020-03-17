package net.ssehub.mutator.mutation.genetic.mutations; 

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Statement;

public class ElementReplacerTest {

    @Test
    public void testReplaceInBlock() {
        Block block = new Block(null);
        
        Statement st1 = createLiteralExpressionStatement("stmt1", block);
        Statement st2 = createLiteralExpressionStatement("stmt2", block);
        Statement st3 = createLiteralExpressionStatement("stmt2", block);
        Statement st4 = createLiteralExpressionStatement("stmt4", block);
        
        block.statements.add(st1);
        block.statements.add(st2);
        block.statements.add(st3);
        block.statements.add(st4);
        
        Statement replacement = createLiteralExpressionStatement("stmtNEW2", block);
        
        ElementReplacer<Statement> replacer = new ElementReplacer<>();
        boolean success = replacer.replace(st2, replacement);
        
        assertThat(success, is(true));
        assertThat(block.statements.get(0), sameInstance(st1));
        assertThat(block.statements.get(1), sameInstance(replacement));
        assertThat(block.statements.get(2), sameInstance(st3));
        assertThat(block.statements.get(3), sameInstance(st4));
    }
    
    private static ExpressionStmt createLiteralExpressionStatement(String content, AstElement parent) {
        ExpressionStmt stmt = new ExpressionStmt(parent);
        
        Literal lit = new Literal(stmt);
        lit.value = content;
        
        stmt.expr = lit;
        
        return stmt;
    }
    
}
