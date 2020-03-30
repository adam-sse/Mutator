package net.ssehub.mutator.ast.operations;

import java.util.StringJoiner;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.For;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.FunctionDecl;
import net.ssehub.mutator.ast.If;
import net.ssehub.mutator.ast.JumpStmt;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.While;

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
    public String visitBlock(Block stmt) {
        StringBuilder sb = new StringBuilder();
        sb.append(indentation(stmt.id)).append("{\n");

        this.indentation++;
        for (Statement st : stmt.statements) {
            sb.append(st.accept(this));
        }
        this.indentation--;

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
            this.indentation++;
            sb.append(stmt.body.accept(this));
            this.indentation--;
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

        for (AstElement func : file.functions) {
            sj.add(func.accept(this));
        }

        return sj.toString();
    }

    @Override
    public String visitFor(For stmt) {
        StringBuilder sb = new StringBuilder();

        sb.append(indentation(stmt.id)).append("for (");

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
        sb.append(")\n");

        if (stmt.body instanceof Block) {
            sb.append(stmt.body.accept(this));
        } else {
            this.indentation++;
            sb.append(stmt.body.accept(this));
            this.indentation--;
        }

        return sb.toString();
    }

    @Override
    public String visitFunction(Function func) {
        return indentation(func.id) + func.header.accept(this) + "\n" + func.body.accept(this);
    }

    @Override
    public String visitFunctionDecl(FunctionDecl decl) {
        String line = super.visitFunctionDecl(decl);
        if (decl.parent instanceof Function)
            return line;
        else
            return indentation(decl.id) + line + "\n";
    }

    @Override
    public String visitIf(If stmt) {
        StringBuilder sb = new StringBuilder();

        sb.append(indentation(stmt.id)).append("if (").append(stmt.condition.accept(this)).append(")\n");

        if (stmt.thenBlock instanceof Block) {
            sb.append(stmt.thenBlock.accept(this));
        } else {
            this.indentation++;
            sb.append(stmt.thenBlock.accept(this));
            this.indentation--;
        }

        if (stmt.elseBlock != null) {
            sb.append(indentation(null)).append("else\n");

            if (stmt.elseBlock instanceof Block) {
                sb.append(stmt.elseBlock.accept(this));
            } else {
                this.indentation++;
                sb.append(stmt.elseBlock.accept(this));
                this.indentation--;
            }
        }

        return sb.toString();
    }

    @Override
    public String visitJumpStmt(JumpStmt stmt) {
        return indentation(stmt.id) + super.visitJumpStmt(stmt) + "\n";
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
            this.indentation++;
            sb.append(stmt.body.accept(this));
            this.indentation--;
        }

        return sb.toString();
    }

}
