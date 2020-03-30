package net.ssehub.mutator.mutation.genetic.mutations;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.For;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.FunctionCall;
import net.ssehub.mutator.ast.FunctionDecl;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.If;
import net.ssehub.mutator.ast.JumpStmt;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.While;
import net.ssehub.mutator.ast.operations.IAstVisitor;
import net.ssehub.mutator.util.Util;

public class ElementReplacer<T extends AstElement> implements IAstVisitor<Boolean> {

    private T toReplace;

    private T replacement;

    public boolean replace(T toReplace, T replacement) {
        this.toReplace = toReplace;
        this.replacement = replacement;
        return toReplace.parent.accept(this);
    }

    @Override
    public Boolean visitBinaryExpr(BinaryExpr expr) {
        if (this.toReplace == expr.left) {
            expr.left = (Expression) this.replacement;
            return true;
        }

        if (this.toReplace == expr.right) {
            expr.right = (Expression) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitBlock(Block stmt) {
        int i = Util.findIndex(stmt.statements, (Statement) this.toReplace);
        if (i != -1) {
            stmt.statements.set(i, (Statement) this.replacement);
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitDeclaration(Declaration decl) {
        if (this.toReplace == decl.type) {
            decl.type = (Type) this.replacement;
            return true;
        } else if (this.toReplace == decl.initExpr) {
            decl.initExpr = (Expression) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitDeclarationStmt(DeclarationStmt stmt) {
        if (this.toReplace == stmt.decl) {
            stmt.decl = (Declaration) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitDoWhileLoop(DoWhileLoop stmt) {
        if (this.toReplace == stmt.condition) {
            stmt.condition = (Expression) this.replacement;
            return true;
        }

        if (this.toReplace == stmt.body) {
            stmt.body = (Statement) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitEmptyStmt(EmptyStmt stmt) {
        return false;
    }

    @Override
    public Boolean visitExpressionStmt(ExpressionStmt stmt) {
        if (this.toReplace == stmt.expr) {
            stmt.expr = (Expression) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitFile(File file) {
        int i = Util.findIndex(file.functions, (Function) this.toReplace);
        if (i != -1) {
            file.functions.set(i, this.replacement);
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitFor(For stmt) {
        if (this.toReplace == stmt.init) {
            stmt.init = (Declaration) this.replacement;
            return true;
        }

        if (this.toReplace == stmt.condition) {
            stmt.condition = (Expression) this.replacement;
            return true;
        }

        if (this.toReplace == stmt.increment) {
            stmt.increment = (Expression) this.replacement;
            return true;
        }

        if (this.toReplace == stmt.body) {
            stmt.body = (Statement) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitFunction(Function func) {
        if (this.toReplace == func.header) {
            func.header = (FunctionDecl) this.replacement;
            return true;
        }

        if (this.toReplace == func.body) {
            func.body = (Block) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitFunctionCall(FunctionCall expr) {
        int i = Util.findIndex(expr.params, (Expression) this.toReplace);
        if (i != -1) {
            expr.params.set(i, (Expression) this.replacement);
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitFunctionDecl(FunctionDecl decl) {
        if (this.toReplace == decl.type) {
            decl.type = (Type) this.replacement;
            return true;
        }

        int i = Util.findIndex(decl.parameters, (Declaration) this.toReplace);
        if (i != -1) {
            decl.parameters.set(i, (Declaration) this.replacement);
            return true;
        }

        return null;
    }

    @Override
    public Boolean visitIdentifier(Identifier expr) {
        return false;
    }

    @Override
    public Boolean visitIf(If stmt) {
        if (this.toReplace == stmt.condition) {
            stmt.condition = (Expression) this.replacement;
            return true;
        }

        if (this.toReplace == stmt.thenBlock) {
            stmt.thenBlock = (Statement) this.replacement;
            return true;
        }

        if (this.toReplace == stmt.elseBlock) {
            stmt.elseBlock = (Statement) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitJumpStmt(JumpStmt stmt) {
        return false;
    }

    @Override
    public Boolean visitLiteral(Literal expr) {
        return false;
    }

    @Override
    public Boolean visitReturn(Return stmt) {
        if (this.toReplace == stmt.value) {
            stmt.value = (Expression) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitType(Type type) {
        return false;
    }

    @Override
    public Boolean visitUnaryExpr(UnaryExpr expr) {
        if (this.toReplace == expr.expr) {
            expr.expr = (Expression) this.replacement;
            return true;
        }

        return false;
    }

    @Override
    public Boolean visitWhile(While stmt) {
        if (this.toReplace == stmt.condition) {
            stmt.condition = (Expression) this.replacement;
            return true;
        }

        if (this.toReplace == stmt.body) {
            stmt.body = (Statement) this.replacement;
            return true;
        }

        return false;
    }

}
