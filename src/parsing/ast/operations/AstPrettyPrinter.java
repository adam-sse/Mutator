package parsing.ast.operations;

import java.util.StringJoiner;

import parsing.ast.Assignment;
import parsing.ast.Block;
import parsing.ast.Declaration;
import parsing.ast.DeclarationStmt;
import parsing.ast.DoWhileLoop;
import parsing.ast.EmptyStmt;
import parsing.ast.ExpressionStmt;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.If;
import parsing.ast.Return;
import parsing.ast.Statement;
import parsing.ast.While;

public class AstPrettyPrinter extends AbstractPrinter {

    private int indentation;
    
    private boolean writeIds;
    
    public AstPrettyPrinter(boolean writeIds) {
        this.writeIds = writeIds;
    }
    
    private String indentation(Long id) {
        StringBuilder sb = new StringBuilder();
        
        if (this.writeIds) {
            final int idWidth = 12;
            if (id != null) {
                String idStr = String.format("/*#%d*/ ", id);
                sb.append(" ".repeat(Math.max(idWidth - idStr.length(), 0))).append(idStr);
            } else {
                sb.append(" ".repeat(idWidth));
            }
        }
        
        sb.append("\t".repeat(this.indentation));
        return sb.toString();
    }
    
    @Override
    public String visitAssignment(Assignment stmt) {
        return indentation(stmt.id) + super.visitAssignment(stmt) + "\n";
    }

    @Override
    public String visitBlock(Block stmt) {
        StringBuilder sb = new StringBuilder();
        sb.append(indentation(stmt.id)).append("{\n");
        
        indentation++;
        for (Statement st : stmt.statements) {
            sb.append(st.accept(this));
        }
        indentation--;
        
        sb.append(indentation(null)).append("}\n");
        
        return sb.toString();
    }

    @Override
    public String visitDeclarationStmt(DeclarationStmt stmt) {
        return indentation(stmt.id) + super.visitDeclarationStmt(stmt) + "\n";
    }

    @Override
    public String visitDoWhileLoop(DoWhileLoop stmt) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(indentation(stmt.id)).append("do\n");
        
        if (stmt.body instanceof Block) {
            sb.append(stmt.body.accept(this));
            // replace trailing \n with " "
            sb.delete(sb.length() - 1, sb.length());
            sb.append(" ");
            
        } else {
            indentation++;
            sb.append(stmt.body.accept(this));
            indentation--;
            sb.append(indentation(null));
        }
        
        sb.append("while (").append(stmt.condition.accept(this)).append(");\n");
        
        return sb.toString();
    }

    @Override
    public String visitEmptyStmt(EmptyStmt stmt) {
        return indentation(stmt.id) + super.visitEmptyStmt(stmt) + "\n";
    }

    @Override
    public String visitExpressionStmt(ExpressionStmt stmt) {
        return indentation(stmt.id) + super.visitExpressionStmt(stmt) + "\n";
    }

    @Override
    public String visitFile(File file) {
        StringJoiner sj = new StringJoiner("\n"); // extra spacing around functions
        
        for (Function func : file.functions) {
            sj.add(func.accept(this));
        }
        
        return sj.toString();
    }
    
    @Override
    public String visitFunction(Function func) {
        StringBuilder sb = new StringBuilder();
        sb.append(indentation(func.id)).append(func.type.accept(this)).append(" ").append(func.name).append("(");
        
        StringJoiner sj = new StringJoiner(", ");
        for (Declaration decl : func.parameters) {
            sj.add(decl.accept(this));
        }
        sb.append(sj.toString()).append(")\n").append(func.body.accept(this));
        
        return sb.toString();
    }

    @Override
    public String visitIf(If stmt) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(indentation(stmt.id)).append("if (").append(stmt.condition.accept(this)).append(")\n");
        
        if (stmt.thenBlock instanceof Block) {
            sb.append(stmt.thenBlock.accept(this));
        } else {
            indentation++;
            sb.append(stmt.thenBlock.accept(this));
            indentation--;
        }
        
        if (stmt.elseBlock != null) {
            sb.append(indentation(null)).append("else\n");
            
            if (stmt.elseBlock instanceof Block) {
                sb.append(stmt.elseBlock.accept(this));
            } else {
                indentation++;
                sb.append(stmt.elseBlock.accept(this));
                indentation--;
            }
        }
        
        return sb.toString();
    }

    @Override
    public String visitReturn(Return stmt) {
        return indentation(stmt.id) + super.visitReturn(stmt) + "\n";
    }

    @Override
    public String visitWhile(While stmt) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(indentation(stmt.id)).append("while (").append(stmt.condition.accept(this)).append(")\n");
        
        if (stmt.body instanceof Block) {
            sb.append(stmt.body.accept(this));
        } else {
            indentation++;
            sb.append(stmt.body.accept(this));
            indentation--;
        }
        
        return sb.toString();
    }

}
