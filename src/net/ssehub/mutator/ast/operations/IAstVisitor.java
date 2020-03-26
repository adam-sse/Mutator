package net.ssehub.mutator.ast.operations;

import net.ssehub.mutator.ast.Declaration;
import net.ssehub.mutator.ast.File;
import net.ssehub.mutator.ast.Function;
import net.ssehub.mutator.ast.FunctionDecl;
import net.ssehub.mutator.ast.Type;

public interface IAstVisitor<T> extends IExpressionVisitor<T>, IStatementVisitor<T> {

    public T visitDeclaration(Declaration decl);
    
    public T visitFile(File file);
    
    public T visitFunction(Function func);
    
    public T visitFunctionDecl(FunctionDecl decl);
    
    public T visitType(Type type);
    
}
