package net.ssehub.mutator.ast.operations;

import net.ssehub.mutator.ast.Assignment;
import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.For;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.FunctionCall;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.If;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.Type;
import net.ssehub.mutator.ast.UnaryExpr;
import net.ssehub.mutator.ast.While;

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
    
    public T visitFor(For stmt);
    
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
