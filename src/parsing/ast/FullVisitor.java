package parsing.ast;

public class FullVisitor implements IAstVisitor {
    
    private IAstVisitor other;
    
    public FullVisitor(IAstVisitor other) {
        this.other = other;
    }

    @Override
    public void visitAssignment(Assignment stmt) {
        other.visitAssignment(stmt);
        
        stmt.variable.accept(this);
        stmt.value.accept(this);
    }

    @Override
    public void visitBinaryExpr(BinaryExpr expr) {
        other.visitBinaryExpr(expr);
        
        expr.left.accept(this);
        expr.right.accept(this);
    }

    @Override
    public void visitBlock(Block stmt) {
        other.visitBlock(stmt);
        
        for (Statement child : stmt.statements) {
            child.accept(this);
        }
    }

    @Override
    public void visitDeclaration(Declaration decl) {
        other.visitDeclaration(decl);
        
        decl.type.accept(this);
    }

    @Override
    public void visitDeclarationStmt(DeclarationStmt stmt) {
        other.visitDeclarationStmt(stmt);
        
        stmt.decl.accept(this);
    }

    @Override
    public void visitDoWhileLoop(DoWhileLoop stmt) {
        other.visitDoWhileLoop(stmt);
        
        stmt.condition.accept(this);
        stmt.body.accept(this);
    }

    @Override
    public void visitEmptyStmt(EmptyStmt stmt) {
        other.visitEmptyStmt(stmt);
    }

    @Override
    public void visitExpressionStmt(ExpressionStmt stmt) {
        other.visitExpressionStmt(stmt);
        
        stmt.expr.accept(this);
    }

    @Override
    public void visitFile(File file) {
        other.visitFile(file);
        
        for (Function f : file.functions) {
            f.accept(this);
        }
    }

    @Override
    public void visitFunction(Function func) {
        other.visitFunction(func);
        
        func.type.accept(this);
        for (Declaration decl : func.parameters) {
            decl.accept(this);
        }
        func.body.accept(this);
    }

    @Override
    public void visitFunctionCall(FunctionCall expr) {
        other.visitFunctionCall(expr);
        
        for (Expression param : expr.params) {
            param.accept(this);
        }
    }

    @Override
    public void visitIdentifier(Identifier expr) {
        other.visitIdentifier(expr);
    }

    @Override
    public void visitIf(If stmt) {
        other.visitIf(stmt);
        
        stmt.condition.accept(this);
        stmt.thenBlock.accept(this);
        if (stmt.elseBlock != null) {
            stmt.elseBlock.accept(this);
        }
    }

    @Override
    public void visitLiteral(Literal expr) {
        other.visitLiteral(expr);
    }

    @Override
    public void visitReturn(Return stmt) {
        other.visitReturn(stmt);
        
        if (stmt.value != null) {
            stmt.value.accept(this);
        }
    }

    @Override
    public void visitType(Type type) {
        other.visitType(type);
    }

    @Override
    public void visitUnaryExpr(UnaryExpr expr) {
        other.visitUnaryExpr(expr);
        
        expr.expr.accept(this);
    }

    @Override
    public void visitWhile(While stmt) {
        other.visitWhile(stmt);
        
        stmt.condition.accept(this);
        stmt.body.accept(this);
    }

}
