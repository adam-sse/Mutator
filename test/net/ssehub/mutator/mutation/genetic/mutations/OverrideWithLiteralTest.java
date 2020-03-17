package net.ssehub.mutator.mutation.genetic.mutations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.ssehub.mutator.ast.AstElement.Location;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.BinaryOperator;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.UnaryOperator;
import net.ssehub.mutator.ast.operations.AstCloner;
import net.ssehub.mutator.mutation.genetic.MutationIdentifier;

public class OverrideWithLiteralTest {

    private Expression createLiteral(int value, boolean negated) {
        Literal lit = new Literal(null);
        lit.value = Integer.toString(value);
        
        Expression result = lit;
        if (negated) {
            UnaryExpr op = new UnaryExpr(null);
            op.operator = UnaryOperator.MINUS;
            op.expr = lit;
            
            lit.parent = op;
            result = op;
        }
        
        return result;
    }
    
    @Test
    public void testEquals() {
        MutationIdentifier id1 = new MutationIdentifier(123);
        OverrideWithLiteral mut1 = new OverrideWithLiteral(id1, createLiteral(4, false));
        
        assertThat(mut1.equals(mut1), is(true));
        
        MutationIdentifier id2 = new MutationIdentifier(321);
        OverrideWithLiteral mut2 = new OverrideWithLiteral(id2, createLiteral(4, false));
        assertThat(mut1.equals(mut2), is(false));
        
        MutationIdentifier id5 = new MutationIdentifier(123);
        OverrideWithLiteral mut5 = new OverrideWithLiteral(id5, createLiteral(4, false));
        assertThat(mut1.equals(mut5), is(true));
        
        OverrideWithLiteral mut6 = new OverrideWithLiteral(id1, createLiteral(4, false));
        assertThat(mut1.equals(mut6), is(true));
        
        OverrideWithLiteral mut7 = new OverrideWithLiteral(id1, createLiteral(3, false));
        assertThat(mut1.equals(mut7), is(false));
        
        OverrideWithLiteral mut8 = new OverrideWithLiteral(id1, createLiteral(4, true));
        assertThat(mut1.equals(mut8), is(false));
    }
    
    @Test
    public void testDoubleApply() {
        ExpressionStmt stmt = new ExpressionStmt(null);
        stmt.start = new Location(1, 1);
        stmt.end = new Location(1, 30);
        
        BinaryExpr expr = new BinaryExpr(stmt);
        expr.start = new Location(1, 1);
        expr.end = new Location(1, 29);
        expr.operator = BinaryOperator.ADDITION;
        
        Identifier var1 = new Identifier(expr);
        var1.start = new Location(1, 4);
        var1.end = new Location(1, 10);
        var1.identifier = "A";
        
        Identifier var2 = new Identifier(expr);
        var2.start = new Location(1, 14);
        var2.end = new Location(1, 29);
        var2.identifier = "B";
        
        expr.left = var1;
        expr.right = var2;
        stmt.expr = expr;
        
        assertThat(stmt.getText(), is("A + B;"));
        
        OverrideWithLiteral mut = new OverrideWithLiteral(new MutationIdentifier(var2), createLiteral(3, true));
        
        boolean applied = mut.apply(stmt);
        assertThat(applied, is(true));
        assertThat(stmt.getText(), is("A + -3;"));
        
        applied = mut.apply(stmt);
        assertThat(applied, is(false));
        assertThat(stmt.getText(), is("A + -3;"));
    }
    
    @Test
    public void testDoubleApplyOnLiteral() {
        ExpressionStmt stmt = new ExpressionStmt(null);
        stmt.start = new Location(1, 1);
        stmt.end = new Location(1, 30);
        
        BinaryExpr expr = new BinaryExpr(stmt);
        expr.start = new Location(1, 1);
        expr.end = new Location(1, 29);
        expr.operator = BinaryOperator.ADDITION;
        
        Identifier var1 = new Identifier(expr);
        var1.start = new Location(1, 4);
        var1.end = new Location(1, 10);
        var1.identifier = "A";
        
        Literal lit = new Literal(expr);
        lit.start = new Location(1, 14);
        lit.end = new Location(1, 29);
        lit.value = "3";
        
        expr.left = var1;
        expr.right = lit;
        stmt.expr = expr;
        
        assertThat(stmt.getText(), is("A + 3;"));
        
        OverrideWithLiteral mut = new OverrideWithLiteral(new MutationIdentifier(lit), createLiteral(5, true));
        
        boolean applied = mut.apply(stmt);
        assertThat(applied, is(true));
        assertThat(stmt.getText(), is("A + -5;"));
        
        applied = mut.apply(stmt);
        assertThat(applied, is(false));
        assertThat(stmt.getText(), is("A + -5;"));
    }
    
    @Test
    public void testReproduceIds() {
        ExpressionStmt stmt = new ExpressionStmt(null);
        stmt.start = new Location(1, 1);
        stmt.end = new Location(1, 30);
        
        BinaryExpr expr = new BinaryExpr(stmt);
        expr.start = new Location(1, 1);
        expr.end = new Location(1, 29);
        expr.operator = BinaryOperator.ADDITION;
        
        Identifier var1 = new Identifier(expr);
        var1.start = new Location(1, 4);
        var1.end = new Location(1, 10);
        var1.identifier = "A";
        
        Literal lit = new Literal(expr);
        lit.start = new Location(1, 14);
        lit.end = new Location(1, 29);
        lit.value = "3";
        
        expr.left = var1;
        expr.right = lit;
        stmt.expr = expr;
        
        long originalId = stmt.getChild(0).getChild(1).id;
        
        ExpressionStmt clone = new AstCloner(null, true).visitExpressionStmt(stmt);
        assertThat(stmt.getText(), is("A + 3;"));
        assertThat(clone.getText(), is("A + 3;"));
        
        long originalCloneId = clone.getChild(0).getChild(1).id;
        assertThat(originalCloneId, is(originalId));
        
        OverrideWithLiteral mut = new OverrideWithLiteral(new MutationIdentifier(lit), createLiteral(5, true));
        
        boolean applied = mut.apply(stmt);
        assertThat(applied, is(true));
        assertThat(stmt.getText(), is("A + -5;"));
        assertThat(clone.getText(), is("A + 3;"));
        
        long afterApplyId = stmt.getChild(0).getChild(1).id;
        assertThat(afterApplyId, not(is(originalId)));
        
        applied = mut.apply(clone);
        assertThat(applied, is(true));
        assertThat(stmt.getText(), is("A + -5;"));
        assertThat(clone.getText(), is("A + -5;"));
        
        long afterApplyCloneId = clone.getChild(0).getChild(1).id;
        assertThat(afterApplyCloneId, is(afterApplyId));
    }
    
}
