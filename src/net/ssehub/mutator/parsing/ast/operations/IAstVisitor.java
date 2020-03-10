package net.ssehub.mutator.parsing.ast.operations;

import net.ssehub.mutator.parsing.ast.Assignment;
import net.ssehub.mutator.parsing.ast.BinaryExpr;
import net.ssehub.mutator.parsing.ast.Block;
import net.ssehub.mutator.parsing.ast.Declaration;
import net.ssehub.mutator.parsing.ast.DeclarationStmt;
import net.ssehub.mutator.parsing.ast.DoWhileLoop;
import net.ssehub.mutator.parsing.ast.EmptyStmt;
import net.ssehub.mutator.parsing.ast.ExpressionStmt;
import net.ssehub.mutator.parsing.ast.File;
import net.ssehub.mutator.parsing.ast.Function;
import net.ssehub.mutator.parsing.ast.FunctionCall;
import net.ssehub.mutator.parsing.ast.Identifier;
import net.ssehub.mutator.parsing.ast.If;
import net.ssehub.mutator.parsing.ast.Literal;
import net.ssehub.mutator.parsing.ast.Return;
import net.ssehub.mutator.parsing.ast.Type;
import net.ssehub.mutator.parsing.ast.UnaryExpr;
import net.ssehub.mutator.parsing.ast.While;

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
