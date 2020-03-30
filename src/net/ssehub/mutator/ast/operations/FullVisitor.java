package net.ssehub.mutator.ast.operations;

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

public class FullVisitor implements IAstVisitor<Void> {

    private IAstVisitor<?> other;

    public FullVisitor(IAstVisitor<?> other) {
        this.other = other;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        this.other.visitBinaryExpr(expr);

        expr.left.accept(this);
        expr.right.accept(this);

        return null;
    }

    @Override
    public Void visitBlock(Block stmt) {
        this.other.visitBlock(stmt);

        for (Statement child : stmt.statements) {
            child.accept(this);
        }

        return null;
    }

    @Override
    public Void visitDeclaration(Declaration decl) {
        this.other.visitDeclaration(decl);

        decl.type.accept(this);
        if (decl.initExpr != null) {
            decl.initExpr.accept(this);
        }

        return null;
    }

    @Override
    public Void visitDeclarationStmt(DeclarationStmt stmt) {
        this.other.visitDeclarationStmt(stmt);

        stmt.decl.accept(this);

        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoop stmt) {
        this.other.visitDoWhileLoop(stmt);

        stmt.condition.accept(this);
        stmt.body.accept(this);

        return null;
    }

    @Override
    public Void visitEmptyStmt(EmptyStmt stmt) {
        this.other.visitEmptyStmt(stmt);

        return null;
    }

    @Override
    public Void visitExpressionStmt(ExpressionStmt stmt) {
        this.other.visitExpressionStmt(stmt);

        stmt.expr.accept(this);

        return null;
    }

    @Override
    public Void visitFile(File file) {
        this.other.visitFile(file);

        for (AstElement f : file.functions) {
            f.accept(this);
        }

        return null;
    }

    @Override
    public Void visitFor(For stmt) {
        this.other.visitFor(stmt);

        if (stmt.init != null) {
            stmt.init.accept(this);
        }
        if (stmt.condition != null) {
            stmt.condition.accept(this);
        }
        if (stmt.increment != null) {
            stmt.increment.accept(this);
        }

        stmt.body.accept(this);

        return null;
    }

    @Override
    public Void visitFunction(Function func) {
        this.other.visitFunction(func);

        func.header.accept(this);
        func.body.accept(this);

        return null;
    }

    @Override
    public Void visitFunctionCall(FunctionCall expr) {
        this.other.visitFunctionCall(expr);

        for (Expression param : expr.params) {
            param.accept(this);
        }

        return null;
    }

    @Override
    public Void visitFunctionDecl(FunctionDecl decl) {
        this.other.visitFunctionDecl(decl);

        decl.type.accept(this);
        for (Declaration param : decl.parameters) {
            param.accept(this);
        }

        return null;
    }

    @Override
    public Void visitIdentifier(Identifier expr) {
        this.other.visitIdentifier(expr);

        return null;
    }

    @Override
    public Void visitIf(If stmt) {
        this.other.visitIf(stmt);

        stmt.condition.accept(this);
        stmt.thenBlock.accept(this);
        if (stmt.elseBlock != null) {
            stmt.elseBlock.accept(this);
        }

        return null;
    }

    @Override
    public Void visitJumpStmt(JumpStmt stmt) {
        this.other.visitJumpStmt(stmt);

        return null;
    }

    @Override
    public Void visitLiteral(Literal expr) {
        this.other.visitLiteral(expr);

        return null;
    }

    @Override
    public Void visitReturn(Return stmt) {
        this.other.visitReturn(stmt);

        if (stmt.value != null) {
            stmt.value.accept(this);
        }

        return null;
    }

    @Override
    public Void visitType(Type type) {
        this.other.visitType(type);

        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr expr) {
        this.other.visitUnaryExpr(expr);

        expr.expr.accept(this);

        return null;
    }

    @Override
    public Void visitWhile(While stmt) {
        this.other.visitWhile(stmt);

        stmt.condition.accept(this);
        stmt.body.accept(this);

        return null;
    }

}
