package parsing.ast.operations;

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
import parsing.ast.Type;
import parsing.ast.UnaryExpr;
import parsing.ast.While;

public interface IAstVisitor<T> {

    public T visitAssignment(Assignment stmt);
    
    public T visitBinaryExpr(BinaryExpr expr);
    
    public T visitBlock(Block stmt);
    
    public T visitDeclaration(Declaration decl);
    
    public T visitDeclarationStmt(DeclarationStmt stmt);
    
    public T visitDoWhileLoop(DoWhileLoop stmt);
    
    public T visitEmptyStmt(EmptyStmt stmt);
    
    public T visitExpressionStmt(ExpressionStmt stmt);
    
    public T visitFile(File file);
    
    public T visitFunction(Function func);
    
    public T visitFunctionCall(FunctionCall expr);
    
    public T visitIdentifier(Identifier expr);
    
    public T visitIf(If stmt);
    
    public T visitLiteral(Literal expr);
    
    public T visitReturn(Return stmt);
    
    public T visitType(Type type);
    
    public T visitUnaryExpr(UnaryExpr expr);
    
    public T visitWhile(While stmt);
    
}
