package net.ssehub.mutator.mutation.mutations;

import net.ssehub.mutator.parsing.ast.Assignment;
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
import net.ssehub.mutator.parsing.ast.Statement;
import net.ssehub.mutator.parsing.ast.Type;
import net.ssehub.mutator.parsing.ast.UnaryExpr;
import net.ssehub.mutator.parsing.ast.While;
import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;
import net.ssehub.mutator.util.Util;

class StatementDeleter implements IAstVisitor<Boolean> {
    
    private Statement target;
    
    public boolean delete(Statement target) {
        this.target = target;
        
        return target.parent.accept(this);
    }

    @Override
    public Boolean visitAssignment(Assignment stmt) {
        return false;
    }

    @Override
    public Boolean visitBinaryExpr(BinaryExpr expr) {
        return false;
    }

    @Override
    public Boolean visitBlock(Block stmt) {
        int i = Util.findIndex(stmt.statements, target);
        if (i != -1) {
            stmt.statements.remove(i);
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitDeclaration(Declaration decl) {
        return false;
    }

    @Override
    public Boolean visitDeclarationStmt(DeclarationStmt stmt) {
        return false;
    }

    @Override
    public Boolean visitDoWhileLoop(DoWhileLoop stmt) {
        if (target == stmt.body) {
            EmptyStmt replacement = new EmptyStmt(stmt);
            replacement.start = stmt.body.start;
            replacement.end = stmt.body.end;
            stmt.body = replacement;
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
        return false;
    }

    @Override
    public Boolean visitFile(File file) {
        return false;
    }

    @Override
    public Boolean visitFunction(Function func) {
        if (target == func.body) {
            Block replacement = new Block(func);
            replacement.start = func.body.start;
            replacement.end = func.body.end;
            
            func.body = replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitFunctionCall(FunctionCall expr) {
        return false;
    }

    @Override
    public Boolean visitIdentifier(Identifier expr) {
        return false;
    }

    @Override
    public Boolean visitIf(If stmt) {
        if (target == stmt.thenBlock) {
            EmptyStmt replacement = new EmptyStmt(stmt);
            replacement.start = stmt.thenBlock.start;
            replacement.end = stmt.thenBlock.end;
            stmt.thenBlock = replacement;
            return true;
        }
        
        if (target == stmt.elseBlock) {
            EmptyStmt replacement = new EmptyStmt(stmt);
            replacement.start = stmt.elseBlock.start;
            replacement.end = stmt.elseBlock.end;
            stmt.elseBlock = replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitLiteral(Literal expr) {
        return false;
    }

    @Override
    public Boolean visitReturn(Return stmt) {
        return false;
    }

    @Override
    public Boolean visitType(Type type) {
        return false;
    }

    @Override
    public Boolean visitUnaryExpr(UnaryExpr expr) {
        return false;
    }

    @Override
    public Boolean visitWhile(While stmt) {
        if (target == stmt.body) {
            EmptyStmt replacement = new EmptyStmt(stmt);
            replacement.start = stmt.body.start;
            replacement.end = stmt.body.end;
            stmt.body = replacement;
            return true;
        }
        
        return false;
    }

}
