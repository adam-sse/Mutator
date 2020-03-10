package net.ssehub.mutator.mutation.mutations;

import net.ssehub.mutator.parsing.ast.Assignment;
import net.ssehub.mutator.parsing.ast.AstElement;
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
import net.ssehub.mutator.parsing.ast.operations.IAstVisitor;
import net.ssehub.mutator.util.Util;

class ElementReplacer<T extends AstElement> implements IAstVisitor<Boolean> {
    
    private T toReplace;
    
    private T replacement;
    
    public boolean replace(T toReplace, T replacement) {
        this.toReplace = toReplace;
        this.replacement = replacement;
        return toReplace.parent.accept(this);
    }

    @Override
    public Boolean visitAssignment(Assignment stmt) {
        if (toReplace == stmt.variable) {
            stmt.variable = (Expression) replacement;
            return true;
        }
        
        if (toReplace == stmt.value) {
            stmt.value = (Expression) replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitBinaryExpr(BinaryExpr expr) {
        if (toReplace == expr.left) {
            expr.left = (Expression) replacement;
            return true;
        }
        
        if (toReplace == expr.right) {
            expr.right = (Expression) replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitBlock(Block stmt) {
        int i = Util.findIndex(stmt.statements, (Statement) toReplace);
        if (i != -1) {
            stmt.statements.set(i, (Statement) replacement);
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitDeclaration(Declaration decl) {
        if (toReplace == decl.type) {
            decl.type = (Type) replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitDeclarationStmt(DeclarationStmt stmt) {
        if (toReplace == stmt.decl) {
            stmt.decl = (Declaration) replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitDoWhileLoop(DoWhileLoop stmt) {
        if (toReplace == stmt.condition) {
            stmt.condition = (Expression) replacement;
            return true;
        }
        
        if (toReplace == stmt.body) {
            stmt.body = (Statement) replacement;
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
        if (toReplace == stmt.expr) {
            stmt.expr = (Expression) replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitFile(File file) {
        int i = Util.findIndex(file.functions, (Function) toReplace);
        if (i != -1) {
            file.functions.set(i, (Function) replacement);
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitFunction(Function func) {
        if (toReplace == func.type) {
            func.type = (Type) replacement;
            return true;
        }
        
        if (toReplace == func.body) {
            func.body = (Block) replacement;
            return true;
        }
        
        int i = Util.findIndex(func.parameters, (Declaration) toReplace);
        if (i != -1) {
            func.parameters.set(i, (Declaration) replacement);
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitFunctionCall(FunctionCall expr) {
        int i = Util.findIndex(expr.params, (Expression) toReplace);
        if (i != -1) {
            expr.params.set(i, (Expression) replacement);
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitIdentifier(Identifier expr) {
        return false;
    }

    @Override
    public Boolean visitIf(If stmt) {
        if (toReplace == stmt.condition) {
            stmt.condition = (Expression) replacement;
            return true;
        }
        
        if (toReplace == stmt.thenBlock) {
            stmt.thenBlock = (Statement) replacement;
            return true;
        }
        
        if (toReplace == stmt.elseBlock) {
            stmt.elseBlock = (Statement) replacement;
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
        if (toReplace == stmt.value) {
            stmt.value = (Expression) replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitType(Type type) {
        return false;
    }

    @Override
    public Boolean visitUnaryExpr(UnaryExpr expr) {
        if (toReplace == expr.expr) {
            expr.expr = (Expression) replacement;
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean visitWhile(While stmt) {
        if (toReplace == stmt.condition) {
            stmt.condition = (Expression) replacement;
            return true;
        }
        
        if (toReplace == stmt.body) {
            stmt.body = (Statement) replacement;
            return true;
        }
        
        return false;
    }
    
}