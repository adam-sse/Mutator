package mutation.mutations;

import parsing.ast.Assignment;
import parsing.ast.BinaryExpr;
import parsing.ast.Block;
import parsing.ast.Declaration;
import parsing.ast.DeclarationStmt;
import parsing.ast.DoWhileLoop;
import parsing.ast.EmptyStmt;
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
import parsing.ast.operations.IAstVisitor;
import util.Util;

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
