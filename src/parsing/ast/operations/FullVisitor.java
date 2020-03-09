package parsing.ast.operations;

import parsing.ast.Assignment;
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
import parsing.ast.Statement;
import parsing.ast.Type;
import parsing.ast.UnaryExpr;
import parsing.ast.While;

public class FullVisitor implements IAstVisitor<Void> {
    
    private IAstVisitor<?> other;
    
    public FullVisitor(IAstVisitor<?> other) {
        this.other = other;
    }

    @Override
    public Void visitAssignment(Assignment stmt) {
        other.visitAssignment(stmt);
        
        stmt.variable.accept(this);
        stmt.value.accept(this);
        
        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        other.visitBinaryExpr(expr);
        
        expr.left.accept(this);
        expr.right.accept(this);
        
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
        other.visitDeclaration(decl);
        
        decl.type.accept(this);
        
        return null;
    }

    @Override
    public Void visitDeclarationStmt(DeclarationStmt stmt) {
        other.visitDeclarationStmt(stmt);
        
        stmt.decl.accept(this);
        
        return null;
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoop stmt) {
        other.visitDoWhileLoop(stmt);
        
        stmt.condition.accept(this);
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
        
        stmt.expr.accept(this);
        
        return null;
    }

    @Override
    public Void visitFile(File file) {
        other.visitFile(file);
        
        for (Function f : file.functions) {
            f.accept(this);
        }
        
        return null;
    }

    @Override
    public Void visitFunction(Function func) {
        other.visitFunction(func);
        
        func.type.accept(this);
        for (Declaration decl : func.parameters) {
            decl.accept(this);
        }
        func.body.accept(this);
        
        return null;
    }

    @Override
    public Void visitFunctionCall(FunctionCall expr) {
        other.visitFunctionCall(expr);
        
        for (Expression param : expr.params) {
            param.accept(this);
        }
        
        return null;
    }

    @Override
    public Void visitIdentifier(Identifier expr) {
        other.visitIdentifier(expr);
        
        return null;
    }

    @Override
    public Void visitIf(If stmt) {
        other.visitIf(stmt);
        
        stmt.condition.accept(this);
        stmt.thenBlock.accept(this);
        if (stmt.elseBlock != null) {
            stmt.elseBlock.accept(this);
        }
        
        return null;
    }

    @Override
    public Void visitLiteral(Literal expr) {
        other.visitLiteral(expr);
        
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
        other.visitType(type);
        
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr expr) {
        other.visitUnaryExpr(expr);
        
        expr.expr.accept(this);
        
        return null;
    }

    @Override
    public Void visitWhile(While stmt) {
        other.visitWhile(stmt);
        
        stmt.condition.accept(this);
        stmt.body.accept(this);
        
        return null;
    }

}
