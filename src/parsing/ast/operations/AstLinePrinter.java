package parsing.ast.operations;

import java.util.StringJoiner;

import parsing.ast.Block;
import parsing.ast.Declaration;
import parsing.ast.DoWhileLoop;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.If;
import parsing.ast.Statement;
import parsing.ast.While;

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
        
        for (Function func : file.functions) {
            sj.add(func.accept(this));
        }
        
        return sj.toString();
    }
    
    @Override
    public String visitFunction(Function func) {
        StringBuilder sb = new StringBuilder();
        sb.append(func.type.accept(this)).append(" ").append(func.name).append("(");
        
        StringJoiner sj = new StringJoiner(", ");
        for (Declaration decl : func.parameters) {
            sj.add(decl.accept(this));
        }
        sb.append(sj.toString()).append(") ").append(func.body.accept(this));
        
        return sb.toString();
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
