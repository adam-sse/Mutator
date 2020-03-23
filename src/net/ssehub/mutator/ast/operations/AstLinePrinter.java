package net.ssehub.mutator.ast.operations;

import java.util.StringJoiner;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.For;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.If;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.While;

public class AstLinePrinter extends AbstractPrinter {

    @Override
    public String visitBlock(Block stmt) {
        StringJoiner sj = new StringJoiner(" ", "{ ", " }");
        
        for (Statement st : stmt.statements) {
            sj.add(st.accept(this));
        }
        
        return sj.toString();
    }

    @Override
    public String visitDoWhileLoop(DoWhileLoop stmt) {
        return "do " + stmt.body.accept(this) + " while (" + stmt.condition.accept(this) + ");";
    }

    @Override
    public String visitFile(File file) {
        StringJoiner sj = new StringJoiner(" ");
        
        for (AstElement func : file.functions) {
            sj.add(func.accept(this));
        }
        
        return sj.toString();
    }
    
    @Override
    public String visitFor(For stmt) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("for (");
        
        if (stmt.init != null) {
            sb.append(stmt.init.accept(this));
        }
        sb.append(';');
        
        if (stmt.condition != null) {
            sb.append(' ').append(stmt.condition.accept(this));
        }
        sb.append(';');
        
        if (stmt.increment != null) {
            sb.append(' ').append(stmt.increment.accept(this));
        }
        sb.append(") ").append(stmt.body.accept(this));
        
        return sb.toString();
    }
    
    @Override
    public String visitFunction(Function func) {
        return func.header.accept(this) + " " + func.body.accept(this);
    }

    @Override
    public String visitIf(If stmt) {
        String result = "if (" + stmt.condition.accept(this) + ") " + stmt.thenBlock.accept(this);
        if (stmt.elseBlock != null) {
            result += " else " + stmt.elseBlock.accept(this);
        }
        return result;
    }

    @Override
    public String visitWhile(While stmt) {
        return "while (" + stmt.condition.accept(this) + ") " + stmt.body.accept(this);
    }

}
