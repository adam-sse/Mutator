package net.ssehub.mutator.mutation.mutations;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.mutator.parsing.ast.Assignment;
import net.ssehub.mutator.parsing.ast.AstElement;
import net.ssehub.mutator.parsing.ast.BinaryExpr;
import net.ssehub.mutator.parsing.ast.Block;
import net.ssehub.mutator.parsing.ast.Declaration;
import net.ssehub.mutator.parsing.ast.DeclarationStmt;
import net.ssehub.mutator.parsing.ast.DoWhileLoop;
import net.ssehub.mutator.parsing.ast.EmptyStmt;
import net.ssehub.mutator.parsing.ast.Expression;
import net.ssehub.mutator.parsing.ast.ExpressionStmt;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;
import net.ssehub.mutator.parsing.ast.FunctionCall;
import net.ssehub.mutator.parsing.ast.Identifier;
import net.ssehub.mutator.parsing.ast.If;
import net.ssehub.mutator.parsing.ast.Literal;
import net.ssehub.mutator.parsing.ast.Return;
import net.ssehub.mutator.parsing.ast.Type;
import net.ssehub.mutator.parsing.ast.UnaryExpr;
import net.ssehub.mutator.parsing.ast.While;
import net.ssehub.mutator.parsing.ast.operations.FullVisitor;
import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;

class ExpressionCollector implements IAstVisitor<Void> {

    private List<Expression> expressions = new LinkedList<>();
    
    public List<Expression> getExpressions() {
        return expressions;
    }
    
    public void collect(AstElement element) {
        FullVisitor visitor = new FullVisitor(this);
        element.accept(visitor);
    }
    
    @Override
    public Void visitAssignment(Assignment stmt) {
        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        expressions.add(expr);
        return null;
    }

    @Override
    public Void visitBlock(Block stmt) {
        return null;
    }

    @Override
    public Void visitDeclaration(Declaration decl) {
        return null;
    }

    @Override
    public Void visitDeclarationStmt(DeclarationStmt stmt) {
        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoop stmt) {
        return null;
    }

    @Override
    public Void visitEmptyStmt(EmptyStmt stmt) {
        return null;
    }

    @Override
    public Void visitExpressionStmt(ExpressionStmt stmt) {
        return null;
    }

    @Override
    public Void visitFile(File file) {
        return null;
    }

    @Override
    public Void visitFunction(Function func) {
        return null;
    }

    @Override
    public Void visitFunctionCall(FunctionCall expr) {
        expressions.add(expr);
        return null;
    }

    @Override
    public Void visitIdentifier(Identifier expr) {
        expressions.add(expr);
        return null;
    }

    @Override
    public Void visitIf(If stmt) {
        return null;
    }

    @Override
    public Void visitLiteral(Literal expr) {
        expressions.add(expr);
        return null;
    }

    @Override
    public Void visitReturn(Return stmt) {
        return null;
    }

    @Override
    public Void visitType(Type type) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr expr) {
        expressions.add(expr);
        return null;
    }

    @Override
    public Void visitWhile(While stmt) {
        return null;
    }
    
}
