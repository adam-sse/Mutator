package mutation.mutations;

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

class ElementReplacer<T extends AstElement> implements IAstVisitor {
    
    private T toReplace;
    
    private T replacement;
    
    boolean success;
    
    public boolean replace(T toReplace, T replacement) {
        this.toReplace = toReplace;
        this.replacement = replacement;
        this.success = false;
        
        toReplace.parent.accept(this);
        
        return this.success;
    }

    @Override
    public void visitAssignment(Assignment stmt) {
        if (toReplace == stmt.variable) {
            stmt.variable = (Expression) replacement;
            success = true;
        } else if (toReplace == stmt.value) {
            stmt.value = (Expression) replacement;
            success = true;
        }
    }

    @Override
    public void visitBinaryExpr(BinaryExpr expr) {
        if (toReplace == expr.left) {
            expr.left = (Expression) replacement;
            success = true;
        } else if (toReplace == expr.right) {
            expr.right = (Expression) replacement;
            success = true;
        }
    }

    @Override
    public void visitBlock(Block stmt) {
        int i = Util.findIndex(stmt.statements, (Statement) toReplace);
        if (i != -1) {
            stmt.statements.set(i, (Statement) replacement);
            success = true;
        }
    }

    @Override
    public void visitDeclaration(Declaration decl) {
        if (toReplace == decl.type) {
            decl.type = (Type) replacement;
            success = true;
        }
    }

    @Override
    public void visitDeclarationStmt(DeclarationStmt stmt) {
        if (toReplace == stmt.decl) {
            stmt.decl = (Declaration) replacement;
            success = true;
        }
    }

    @Override
    public void visitDoWhileLoop(DoWhileLoop stmt) {
        if (toReplace == stmt.condition) {
            stmt.condition = (Expression) replacement;
            success = true;
        } else if (toReplace == stmt.body) {
            stmt.body = (Statement) replacement;
            success = true;
        }
    }

    @Override
    public void visitEmptyStmt(EmptyStmt stmt) {
    }

    @Override
    public void visitExpressionStmt(ExpressionStmt stmt) {
        if (toReplace == stmt.expr) {
            stmt.expr = (Expression) replacement;
            success = true;
        }
    }

    @Override
    public void visitFile(File file) {
        int i = Util.findIndex(file.functions, (Function) toReplace);
        if (i != -1) {
            file.functions.set(i, (Function) replacement);
            success = true;
        }
    }

    @Override
    public void visitFunction(Function func) {
        if (toReplace == func.type) {
            func.type = (Type) replacement;
            success = true;
        } else if (toReplace == func.body) {
            func.body = (Block) replacement;
            success = true;
        } else {
            int i = Util.findIndex(func.parameters, (Declaration) toReplace);
            if (i != -1) {
                func.parameters.set(i, (Declaration) replacement);
                success = true;
            }
        }
        
    }

    @Override
    public void visitFunctionCall(FunctionCall expr) {
        int i = Util.findIndex(expr.params, (Expression) toReplace);
        if (i != -1) {
            expr.params.set(i, (Expression) replacement);
            success = true;
        }
    }

    @Override
    public void visitIdentifier(Identifier expr) {
    }

    @Override
    public void visitIf(If stmt) {
        if (toReplace == stmt.condition) {
            stmt.condition = (Expression) replacement;
            success = true;
        } else if (toReplace == stmt.thenBlock) {
            stmt.thenBlock = (Statement) replacement;
            success = true;
        } else if (toReplace == stmt.elseBlock) {
            stmt.elseBlock = (Statement) replacement;
            success = true;
        }
    }

    @Override
    public void visitLiteral(Literal expr) {
    }

    @Override
    public void visitReturn(Return stmt) {
        if (toReplace == stmt.value) {
            stmt.value = (Expression) replacement;
            success = true;
        }
    }

    @Override
    public void visitType(Type type) {
    }

    @Override
    public void visitUnaryExpr(UnaryExpr expr) {
        if (toReplace == expr.expr) {
            expr.expr = (Expression) replacement;
            success = true;
        }
    }

    @Override
    public void visitWhile(While stmt) {
        if (toReplace == stmt.condition) {
            stmt.condition = (Expression) replacement;
            success = true;
        } else if (toReplace == stmt.body) {
            stmt.body = (Statement) replacement;
            success = true;
        }
    }
    
}
