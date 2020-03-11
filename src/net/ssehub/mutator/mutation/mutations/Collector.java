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

class Collector<T> implements IAstVisitor<Void> {

    private Class<T> type;
    
    private List<T> foundElements = new LinkedList<>();
    
    public Collector(Class<T> type) {
        this.type = type;
    }
    
    public List<T> getFoundElements() {
        return foundElements;
    }
    
    public void collect(AstElement element) {
        FullVisitor visitor = new FullVisitor(this);
        element.accept(visitor);
    }
    
    @SuppressWarnings("unchecked")
    private void check(AstElement element) {
        if (type.isAssignableFrom(element.getClass())) {
            foundElements.add((T) element);
        }
    }
    
    @Override
    public Void visitAssignment(Assignment stmt) {
        check(stmt);
        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        check(expr);
        return null;
    }

    @Override
    public Void visitBlock(Block stmt) {
        check(stmt);
        return null;
    }

    @Override
    public Void visitDeclaration(Declaration decl) {
        check(decl);
        return null;
    }

    @Override
    public Void visitDeclarationStmt(DeclarationStmt stmt) {
        check(stmt);
        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoop stmt) {
        check(stmt);
        return null;
    }

    @Override
    public Void visitEmptyStmt(EmptyStmt stmt) {
        check(stmt);
        return null;
    }

    @Override
    public Void visitExpressionStmt(ExpressionStmt stmt) {
        check(stmt);
        return null;
    }

    @Override
    public Void visitFile(File file) {
        check(file);
        return null;
    }

    @Override
    public Void visitFunction(Function func) {
        check(func);
        return null;
    }

    @Override
    public Void visitFunctionCall(FunctionCall expr) {
        check(expr);
        return null;
    }

    @Override
    public Void visitIdentifier(Identifier expr) {
        check(expr);
        return null;
    }

    @Override
    public Void visitIf(If stmt) {
        check(stmt);
        return null;
    }

    @Override
    public Void visitLiteral(Literal expr) {
        check(expr);
        return null;
    }

    @Override
    public Void visitReturn(Return stmt) {
        check(stmt);
        return null;
    }

    @Override
    public Void visitType(Type type) {
        check(type);
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr expr) {
        check(expr);
        return null;
    }

    @Override
    public Void visitWhile(While stmt) {
        check(stmt);
        return null;
    }
    
}
