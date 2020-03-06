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
import parsing.ast.ExpressionStmt;
import parsing.ast.File;
import parsing.ast.FullVisitor;
import parsing.ast.Function;
import parsing.ast.FunctionCall;
import parsing.ast.IAstVisitor;
import parsing.ast.Identifier;
import parsing.ast.If;
import parsing.ast.Literal;
import parsing.ast.Return;
import parsing.ast.Statement;
import parsing.ast.Type;
import parsing.ast.UnaryExpr;
import parsing.ast.While;

class StatementCollector implements IAstVisitor {

    public List<Statement> statements = new LinkedList<>();
    
    public void collect(AstElement element) {
        FullVisitor visitor = new FullVisitor(this);
        element.accept(visitor);
    }
    
    @Override
    public void visitAssignment(Assignment stmt) {
        statements.add(stmt);
    }

    @Override
    public void visitBinaryExpr(BinaryExpr expr) {
    }

    @Override
    public void visitBlock(Block stmt) {
        statements.add(stmt);
    }

    @Override
    public void visitDeclaration(Declaration decl) {
    }

    @Override
    public void visitDeclarationStmt(DeclarationStmt stmt) {
        statements.add(stmt);
    }

    @Override
    public void visitDoWhileLoop(DoWhileLoop stmt) {
        statements.add(stmt);
    }

    @Override
    public void visitEmptyStmt(EmptyStmt stmt) {
        statements.add(stmt);
    }

    @Override
    public void visitExpressionStmt(ExpressionStmt stmt) {
        statements.add(stmt);
    }

    @Override
    public void visitFile(File file) {
    }

    @Override
    public void visitFunction(Function func) {
    }

    @Override
    public void visitFunctionCall(FunctionCall expr) {
    }

    @Override
    public void visitIdentifier(Identifier expr) {
    }

    @Override
    public void visitIf(If stmt) {
        statements.add(stmt);
    }

    @Override
    public void visitLiteral(Literal expr) {
    }

    @Override
    public void visitReturn(Return stmt) {
        statements.add(stmt);
    }

    @Override
    public void visitType(Type type) {
    }

    @Override
    public void visitUnaryExpr(UnaryExpr expr) {
    }

    @Override
    public void visitWhile(While stmt) {
        statements.add(stmt);
    }
    
}
