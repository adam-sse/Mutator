package net.ssehub.mutator.ast.operations;

import net.ssehub.mutator.ast.BinaryExpr;
import net.ssehub.mutator.ast.FunctionCall;
import net.ssehub.mutator.ast.Identifier;
import net.ssehub.mutator.ast.Literal;
import net.ssehub.mutator.ast.UnaryExpr;

public interface IExpressionVisitor<T> {

    public T visitBinaryExpr(BinaryExpr expr);
    
    public T visitFunctionCall(FunctionCall expr);
    
    public T visitIdentifier(Identifier expr);
    
    public T visitLiteral(Literal expr);
    
    public T visitUnaryExpr(UnaryExpr expr);
    
}
