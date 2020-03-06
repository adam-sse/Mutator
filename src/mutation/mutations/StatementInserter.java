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

class StatementInserter implements IAstVisitor {
    
    private Statement reference;
    
    private Statement toInsert;
    
    private boolean before;
    
    boolean success;
    
    public boolean insert(Statement reference, boolean before, Statement toInsert) {
        this.reference = reference;
        this.toInsert = toInsert;
        this.before = before;
        this.success = false;
        
        reference.parent.accept(this);
        
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
        int refI = Util.findIndex(stmt.statements, reference);
        if (refI != -1) {
            stmt.statements.add(refI + (before ? 0 : 1), toInsert);
            this.success = true;
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
            
            this.success = true;
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
            
            this.success = true;
            
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
            
            this.success = true;
            
        } else if (stmt.elseBlock == reference) {
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
            
            this.success = true;
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
            
            this.success = true;
        }
    }

}
