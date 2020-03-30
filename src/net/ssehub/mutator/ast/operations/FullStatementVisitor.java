package net.ssehub.mutator.ast.operations;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
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

public class FullStatementVisitor implements IAstVisitor<Void> {

    private IStatementVisitor<?> other;

    public FullStatementVisitor(IStatementVisitor<?> other) {
        this.other = other;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        return null;
    }

    @Override
    public Void visitBlock(Block stmt) {
        other.visitBlock(stmt);

        for (Statement child : stmt.statements) {
            child.accept(this);
        }

        return null;
    }

    @Override
    public Void visitDeclaration(Declaration decl) {
        return null;
    }

    @Override
    public Void visitDeclarationStmt(DeclarationStmt stmt) {
        other.visitDeclarationStmt(stmt);

        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoop stmt) {
        other.visitDoWhileLoop(stmt);

        stmt.body.accept(this);

        return null;
    }

    @Override
    public Void visitEmptyStmt(EmptyStmt stmt) {
        other.visitEmptyStmt(stmt);

        return null;
    }

    @Override
    public Void visitExpressionStmt(ExpressionStmt stmt) {
        other.visitExpressionStmt(stmt);

        return null;
    }

    @Override
    public Void visitFile(File file) {
        for (AstElement f : file.functions) {
            f.accept(this);
        }

        return null;
    }

    @Override
    public Void visitFor(For stmt) {
        other.visitFor(stmt);

        stmt.body.accept(this);

        return null;
    }

    @Override
    public Void visitFunction(Function func) {
        func.body.accept(this);

        return null;
    }

    @Override
    public Void visitFunctionCall(FunctionCall expr) {
        return null;
    }

    @Override
    public Void visitFunctionDecl(FunctionDecl decl) {
        return null;
    }

    @Override
    public Void visitIdentifier(Identifier expr) {
        return null;
    }

    @Override
    public Void visitIf(If stmt) {
        other.visitIf(stmt);

        stmt.thenBlock.accept(this);
        if (stmt.elseBlock != null) {
            stmt.elseBlock.accept(this);
        }

        return null;
    }

    @Override
    public Void visitJumpStmt(JumpStmt stmt) {
        other.visitJumpStmt(stmt);

        return null;
    }

    @Override
    public Void visitLiteral(Literal expr) {
        return null;
    }

    @Override
    public Void visitReturn(Return stmt) {
        other.visitReturn(stmt);

        if (stmt.value != null) {
            stmt.value.accept(this);
        }

        return null;
    }

    @Override
    public Void visitType(Type type) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr expr) {
        return null;
    }

    @Override
    public Void visitWhile(While stmt) {
        other.visitWhile(stmt);

        stmt.body.accept(this);

        return null;
    }

}
