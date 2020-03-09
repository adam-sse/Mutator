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

class StatementInserter implements IAstVisitor<Boolean> {
    
    private Statement reference;
    
    private Statement toInsert;
    
    private boolean before;
    
    public boolean insert(Statement reference, boolean before, Statement toInsert) {
        this.reference = reference;
        this.toInsert = toInsert;
        this.before = before;
        
        return reference.parent.accept(this);
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
        int refI = Util.findIndex(stmt.statements, reference);
        if (refI != -1) {
            stmt.statements.add(refI + (before ? 0 : 1), toInsert);
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
        if (stmt.body == reference) {
            Block block = new Block(stmt);
            block.start = stmt.body.start;
            block.end = stmt.body.end;
            stmt.body = block;
            
            if (before) {
                block.statements.add(toInsert);
                block.statements.add(reference);
            } else {
                block.statements.add(reference);
                block.statements.add(toInsert);
            }
            reference.parent = block;
            toInsert.parent = block;
            
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
        if (func.body == reference) {
            Block block = new Block(func);
            block.start = func.body.start;
            block.end = func.body.end;
            func.body = block;
            
            if (before) {
                block.statements.add(toInsert);
                block.statements.add(reference);
            } else {
                block.statements.add(reference);
                block.statements.add(toInsert);
            }
            reference.parent = block;
            toInsert.parent = block;
            
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
        if (stmt.thenBlock == reference) {
            Block block = new Block(stmt);
            block.start = stmt.thenBlock.start;
            block.end = stmt.thenBlock.end;
            stmt.thenBlock = block;
            
            if (before) {
                block.statements.add(toInsert);
                block.statements.add(reference);
            } else {
                block.statements.add(reference);
                block.statements.add(toInsert);
            }
            reference.parent = block;
            toInsert.parent = block;
            
            return true;
            
        }
        
        if (stmt.elseBlock == reference) {
            Block block = new Block(stmt);
            block.start = stmt.elseBlock.start;
            block.end = stmt.elseBlock.end;
            stmt.elseBlock = block;
            
            if (before) {
                block.statements.add(toInsert);
                block.statements.add(reference);
            } else {
                block.statements.add(reference);
                block.statements.add(toInsert);
            }
            reference.parent = block;
            toInsert.parent = block;
            
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
        if (stmt.body == reference) {
            Block block = new Block(stmt);
            block.start = stmt.body.start;
            block.end = stmt.body.end;
            stmt.body = block;
            
            if (before) {
                block.statements.add(toInsert);
                block.statements.add(reference);
            } else {
                block.statements.add(reference);
                block.statements.add(toInsert);
            }
            reference.parent = block;
            toInsert.parent = block;
            
            return true;
        }
        
        return false;
    }

}
