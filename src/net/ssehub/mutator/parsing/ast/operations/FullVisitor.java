package net.ssehub.mutator.parsing.ast.operations;

import net.ssehub.mutator.parsing.ast.Assignment;
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
import net.ssehub.mutator.parsing.ast.Statement;
import net.ssehub.mutator.parsing.ast.Type;
import net.ssehub.mutator.parsing.ast.UnaryExpr;
import net.ssehub.mutator.parsing.ast.While;

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