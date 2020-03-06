package parsing.ast;

public interface IAstVisitor {

    public void visitAssignment(Assignment stmt);
    
    public void visitBinaryExpr(BinaryExpr expr);
    
    public void visitBlock(Block stmt);
    
    public void visitDeclaration(Declaration decl);
    
    public void visitDeclarationStmt(DeclarationStmt stmt);
    
    public void visitDoWhileLoop(DoWhileLoop stmt);
    
    public void visitEmptyStmt(EmptyStmt stmt);
    
    public void visitExpressionStmt(ExpressionStmt stmt);
    
    public void visitFile(File file);
    
    public void visitFunction(Function func);
    
    public void visitFunctionCall(FunctionCall expr);
    
    public void visitIdentifier(Identifier expr);
    
    public void visitIf(If stmt);
    
    public void visitLiteral(Literal expr);
    
    public void visitReturn(Return stmt);
    
    public void visitType(Type type);
    
    public void visitUnaryExpr(UnaryExpr expr);
    
    public void visitWhile(While stmt);
    
}
