package mutation.mutations;

import java.util.LinkedList;
import java.util.List;

import parsing.ast.Assignment;
import parsing.ast.AstElement;
import parsing.ast.BinaryExpr;
import parsing.ast.Block;
import parsing.ast.Declaration;
import parsing.ast.DeclarationStmt;
import parsing.ast.DoWhileLoop;
import parsing.ast.EmptyStmt;
import parsing.ast.Expression;
import parsing.ast.ExpressionStmt;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.FunctionCall;
import parsing.ast.Identifier;
import parsing.ast.If;
import parsing.ast.Literal;
import parsing.ast.Return;
import parsing.ast.Type;
import parsing.ast.UnaryExpr;
import parsing.ast.While;
import parsing.ast.operations.FullVisitor;
import parsing.ast.operations.IAstVisitor;

class ExpressionCollector implements IAstVisitor<Void> {

    public List<Expression> expressions = new LinkedList<>();
    
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
