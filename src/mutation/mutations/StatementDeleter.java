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
import parsing.ast.IAstVisitor;
import parsing.ast.Identifier;
import parsing.ast.If;
import parsing.ast.Literal;
import parsing.ast.Return;
import parsing.ast.Statement;
import parsing.ast.Type;
import parsing.ast.UnaryExpr;
import parsing.ast.While;
import util.Util;

class StatementDeleter implements IAstVisitor {
    
    private Statement target;
    
    boolean success;
    
    public boolean delete(Statement target) {
        this.target = target;
        this.success = false;
        
        target.parent.accept(this);
        
        return this.success;
    }

    @Override
    public void visitAssignment(Assignment stmt) {
    }

    @Override
    public void visitBinaryExpr(BinaryExpr expr) {
    }

    @Override
    public void visitBlock(Block stmt) {
        int i = Util.findIndex(stmt.statements, target);
        if (i != -1) {
            stmt.statements.remove(i);
            success = true;
        }
    }

    @Override
    public void visitDeclaration(Declaration decl) {
    }

    @Override
    public void visitDeclarationStmt(DeclarationStmt stmt) {
    }

    @Override
    public void visitDoWhileLoop(DoWhileLoop stmt) {
        if (target == stmt.body) {
            EmptyStmt replacement = new EmptyStmt(stmt);
            replacement.start = stmt.body.start;
            replacement.end = stmt.body.end;
            stmt.body = replacement;
            success = true;
        }
    }

    @Override
    public void visitEmptyStmt(EmptyStmt stmt) {
    }

    @Override
    public void visitExpressionStmt(ExpressionStmt stmt) {
    }

    @Override
    public void visitFile(File file) {
    }

    @Override
    public void visitFunction(Function func) {
        if (target == func.body) {
            Block replacement = new Block(func);
            replacement.start = func.body.start;
            replacement.end = func.body.end;
            
            func.body = replacement;
            success = true;
        }
    }

    @Override
    public void visitFunctionCall(FunctionCall expr) {
    }

    @Override
    public void visitIdentifier(Identifier expr) {
    }

    @Override
    public void visitIf(If stmt) {
        if (target == stmt.thenBlock) {
            EmptyStmt replacement = new EmptyStmt(stmt);
            replacement.start = stmt.thenBlock.start;
            replacement.end = stmt.thenBlock.end;
            stmt.thenBlock = replacement;
            success = true;
            
        } else if (target == stmt.elseBlock) {
            EmptyStmt replacement = new EmptyStmt(stmt);
            replacement.start = stmt.elseBlock.start;
            replacement.end = stmt.elseBlock.end;
            stmt.elseBlock = replacement;
            success = true;
        }
    }

    @Override
    public void visitLiteral(Literal expr) {
    }

    @Override
    public void visitReturn(Return stmt) {
    }

    @Override
    public void visitType(Type type) {
    }

    @Override
    public void visitUnaryExpr(UnaryExpr expr) {
    }

    @Override
    public void visitWhile(While stmt) {
        if (target == stmt.body) {
            EmptyStmt replacement = new EmptyStmt(stmt);
            replacement.start = stmt.body.start;
            replacement.end = stmt.body.end;
            stmt.body = replacement;
            success = true;
        }
    }

}
