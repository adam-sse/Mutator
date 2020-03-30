package net.ssehub.mutator.ast.operations;

import net.ssehub.mutator.ast.Block;
import net.ssehub.mutator.ast.DeclarationStmt;
import net.ssehub.mutator.ast.DoWhileLoop;
import net.ssehub.mutator.ast.EmptyStmt;
import net.ssehub.mutator.ast.ExpressionStmt;
import net.ssehub.mutator.ast.For;
import net.ssehub.mutator.ast.If;
import net.ssehub.mutator.ast.JumpStmt;
import net.ssehub.mutator.ast.Return;
import net.ssehub.mutator.ast.While;

public interface IStatementVisitor<T> {

    public T visitBlock(Block stmt);

    public T visitDeclarationStmt(DeclarationStmt stmt);

    public T visitDoWhileLoop(DoWhileLoop stmt);

    public T visitEmptyStmt(EmptyStmt stmt);

    public T visitExpressionStmt(ExpressionStmt stmt);

    public T visitFor(For stmt);

    public T visitIf(If stmt);

    public T visitJumpStmt(JumpStmt stmt);

    public T visitReturn(Return stmt);

    public T visitWhile(While stmt);

}
