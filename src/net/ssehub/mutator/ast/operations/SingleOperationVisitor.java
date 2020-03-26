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
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.While;

public abstract class SingleOperationVisitor<T> implements IAstVisitor<T> {

    protected abstract T visit(AstElement element);
    
    @Override
    public T visitBinaryExpr(BinaryExpr expr) {
        return visit(expr);
    }

    @Override
    public T visitFunctionCall(FunctionCall expr) {
        return visit(expr);
    }

    @Override
    public T visitIdentifier(Identifier expr) {
        return visit(expr);
    }

    @Override
    public T visitLiteral(Literal expr) {
        return visit(expr);
    }

    @Override
    public T visitUnaryExpr(UnaryExpr expr) {
        return visit(expr);
    }

    @Override
    public T visitBlock(Block stmt) {
        return visit(stmt);
    }

    @Override
    public T visitDeclarationStmt(DeclarationStmt stmt) {
        return visit(stmt);
    }

    @Override
    public T visitDoWhileLoop(DoWhileLoop stmt) {
        return visit(stmt);
    }

    @Override
    public T visitEmptyStmt(EmptyStmt stmt) {
        return visit(stmt);
    }

    @Override
    public T visitExpressionStmt(ExpressionStmt stmt) {
        return visit(stmt);
    }

    @Override
    public T visitFor(For stmt) {
        return visit(stmt);
    }

    @Override
    public T visitIf(If stmt) {
        return visit(stmt);
    }

    @Override
    public T visitJumpStmt(JumpStmt stmt) {
        return visit(stmt);
    }

    @Override
    public T visitReturn(Return stmt) {
        return visit(stmt);
    }

    @Override
    public T visitWhile(While stmt) {
        return visit(stmt);
    }

    @Override
    public T visitDeclaration(Declaration decl) {
        return visit(decl);
    }

    @Override
    public T visitFile(File file) {
        return visit(file);
    }

    @Override
    public T visitFunction(Function func) {
        return visit(func);
    }

    @Override
    public T visitFunctionDecl(FunctionDecl decl) {
        return visit(decl);
    }

    @Override
    public T visitType(Type type) {
        return visit(type);
    }

}
