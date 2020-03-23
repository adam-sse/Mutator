package net.ssehub.mutator.ast.operations;

import net.ssehub.mutator.ast.AstElement;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
import net.ssehub.mutator.ast.Expression;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.For;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.FunctionCall;
import net.ssehub.mutator.ast.FunctionDecl;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.If;
import net.ssehub.mutator.ast.JumpStmt;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.Statement;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.While;

public class IdFinder implements IAstVisitor<AstElement> {

    private long id;
    
    public IdFinder(long id) {
        this.id = id;
    }
    
    private static AstElement getNonNull(AstElement... elements) {
        for (AstElement element : elements) {
            if (element != null) {
                return element;
            }
        }
        return null;
    }
    
    @Override
    public AstElement visitBinaryExpr(BinaryExpr expr) {
        if (expr.id == this.id) {
            return expr;
        }
        return getNonNull(expr.left.accept(this), expr.right.accept(this));
    }

    @Override
    public AstElement visitBlock(Block stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        
        for (Statement nested : stmt.statements) {
            AstElement result = nested.accept(this);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }

    @Override
    public AstElement visitDeclaration(Declaration decl) {
        if (decl.id == this.id) {
            return decl;
        }
        return getNonNull(decl.type.accept(this), decl.initExpr != null ? decl.initExpr.accept(this) : null);
    }

    @Override
    public AstElement visitDeclarationStmt(DeclarationStmt stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return stmt.decl.accept(this);
    }

    @Override
    public AstElement visitDoWhileLoop(DoWhileLoop stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return getNonNull(stmt.body.accept(this), stmt.condition.accept(this));
    }

    @Override
    public AstElement visitEmptyStmt(EmptyStmt stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return null;
    }

    @Override
    public AstElement visitExpressionStmt(ExpressionStmt stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return stmt.expr.accept(this);
    }

    @Override
    public AstElement visitFile(File file) {
        if (file.id == this.id) {
            return file;
        }
        
        for (AstElement func : file.functions) {
            AstElement found = func.accept(this);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }

    @Override
    public AstElement visitFor(For stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return getNonNull(
            stmt.init != null ? stmt.init.accept(this) : null,
            stmt.condition != null ? stmt.condition.accept(this) : null,
            stmt.increment != null ? stmt.increment.accept(this) : null,
            stmt.body.accept(this)
        );
    }

    @Override
    public AstElement visitFunction(Function func) {
        if (func.id == this.id) {
            return func;
        }
        
        return getNonNull(func.header.accept(this), func.body.accept(this));
    }

    @Override
    public AstElement visitFunctionCall(FunctionCall expr) {
        if (expr.id == this.id) {
            return expr;
        }
        
        for (Expression param : expr.params) {
            AstElement found = param.accept(this);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    @Override
    public AstElement visitFunctionDecl(FunctionDecl decl) {
        if (decl.id == this.id) {
            return decl;
        }
        
        for (Declaration param : decl.parameters) {
            AstElement found = param.accept(this);
            if (found != null) {
                return found;
            }
        }
        
        return decl.type.accept(this);
    }

    @Override
    public AstElement visitIdentifier(Identifier expr) {
        if (expr.id == this.id) {
            return expr;
        }
        return null;
    }

    @Override
    public AstElement visitIf(If stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return getNonNull(stmt.condition.accept(this), stmt.thenBlock.accept(this),
                stmt.elseBlock != null ? stmt.elseBlock.accept(this) : null);
    }

    @Override
    public AstElement visitJumpStmt(JumpStmt stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return null;
    }

    @Override
    public AstElement visitLiteral(Literal expr) {
        if (expr.id == this.id) {
            return expr;
        }
        return null;
    }

    @Override
    public AstElement visitReturn(Return stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return stmt.value != null ? stmt.value.accept(this) : null;
    }

    @Override
    public AstElement visitType(Type type) {
        if (type.id == this.id) {
            return type;
        }
        return null;
    }

    @Override
    public AstElement visitUnaryExpr(UnaryExpr expr) {
        if (expr.id == this.id) {
            return expr;
        }
        return expr.expr.accept(this);
    }

    @Override
    public AstElement visitWhile(While stmt) {
        if (stmt.id == this.id) {
            return stmt;
        }
        return getNonNull(stmt.condition.accept(this), stmt.body.accept(this));
    }

}
