package net.ssehub.mutator.ast.operations;

import java.util.Deque;
import java.util.LinkedList;

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

public class AstCloner implements IAstVisitor<AstElement> {

    private boolean keepIds;

    private Deque<AstElement> parents;

    public AstCloner(AstElement newParent, boolean keepIds) {
        this.keepIds = keepIds;

        this.parents = new LinkedList<>();
        this.parents.add(newParent);
    }

    private <T extends AstElement> T initBasics(T source, T clone) {
        clone.start = source.start;
        clone.end = source.end;
        if (this.keepIds) {
            clone.id = source.id;
        }
        return clone;
    }

    private Expression cloneExpression(Expression source) {
        return (Expression) source.accept(this);
    }

    private Statement cloneStatement(Statement source) {
        return (Statement) source.accept(this);
    }

    @Override
    public BinaryExpr visitBinaryExpr(BinaryExpr expr) {
        BinaryExpr clone = new BinaryExpr(this.parents.peek());
        initBasics(expr, clone);

        this.parents.push(clone);

        clone.operator = expr.operator;
        clone.left = cloneExpression(expr.left);
        clone.right = cloneExpression(expr.right);

        this.parents.pop();
        return clone;
    }

    @Override
    public Block visitBlock(Block stmt) {
        Block clone = new Block(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        for (Statement st : stmt.statements) {
            clone.statements.add(cloneStatement(st));
        }

        this.parents.pop();
        return clone;
    }

    @Override
    public Declaration visitDeclaration(Declaration decl) {
        Declaration clone = new Declaration(this.parents.peek());
        initBasics(decl, clone);

        this.parents.push(clone);

        clone.type = visitType(decl.type);
        clone.identifier = decl.identifier;
        if (decl.initExpr != null) {
            clone.initExpr = cloneExpression(decl.initExpr);
        }

        this.parents.pop();
        return clone;
    }

    @Override
    public DeclarationStmt visitDeclarationStmt(DeclarationStmt stmt) {
        DeclarationStmt clone = new DeclarationStmt(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        clone.decl = visitDeclaration(stmt.decl);

        this.parents.pop();
        return clone;
    }

    @Override
    public DoWhileLoop visitDoWhileLoop(DoWhileLoop stmt) {
        DoWhileLoop clone = new DoWhileLoop(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        clone.condition = cloneExpression(stmt.condition);
        clone.body = cloneStatement(stmt.body);

        this.parents.pop();
        return clone;
    }

    @Override
    public EmptyStmt visitEmptyStmt(EmptyStmt stmt) {
        EmptyStmt clone = new EmptyStmt(this.parents.peek());
        initBasics(stmt, clone);

        return clone;
    }

    @Override
    public ExpressionStmt visitExpressionStmt(ExpressionStmt stmt) {
        ExpressionStmt clone = new ExpressionStmt(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        clone.expr = cloneExpression(stmt.expr);

        this.parents.pop();
        return clone;
    }

    @Override
    public File visitFile(File file) {
        File clone = new File(this.parents.peek());
        initBasics(file, clone);

        this.parents.push(clone);

        for (AstElement func : file.functions) {
            clone.functions.add(func.accept(this));
        }

        this.parents.pop();
        return clone;
    }

    @Override
    public For visitFor(For stmt) {
        For clone = new For(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        if (stmt.init != null) {
            clone.init = visitDeclaration(stmt.init);
        }
        if (stmt.condition != null) {
            clone.condition = cloneExpression(stmt.condition);
        }
        if (stmt.increment != null) {
            clone.increment = cloneExpression(stmt.increment);
        }

        clone.body = cloneStatement(stmt.body);

        this.parents.pop();
        return clone;
    }

    @Override
    public Function visitFunction(Function func) {
        Function clone = new Function(this.parents.peek());
        initBasics(func, clone);

        this.parents.push(clone);

        clone.header = visitFunctionDecl(func.header);
        clone.body = visitBlock(func.body);

        this.parents.pop();
        return clone;
    }

    @Override
    public FunctionCall visitFunctionCall(FunctionCall expr) {
        FunctionCall clone = new FunctionCall(this.parents.peek());
        initBasics(expr, clone);

        this.parents.push(clone);

        clone.function = expr.function;

        for (Expression param : expr.params) {
            clone.params.add(cloneExpression(param));
        }

        this.parents.pop();
        return clone;
    }

    @Override
    public FunctionDecl visitFunctionDecl(FunctionDecl decl) {
        FunctionDecl clone = new FunctionDecl(this.parents.peek());
        initBasics(decl, clone);

        this.parents.push(clone);

        clone.type = visitType(decl.type);
        clone.name = decl.name;
        for (Declaration param : decl.parameters) {
            clone.parameters.add(visitDeclaration(param));
        }

        this.parents.pop();
        return clone;
    }

    @Override
    public Identifier visitIdentifier(Identifier expr) {
        Identifier clone = new Identifier(this.parents.peek());
        initBasics(expr, clone);

        clone.identifier = expr.identifier;

        return clone;
    }

    @Override
    public If visitIf(If stmt) {
        If clone = new If(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        clone.condition = cloneExpression(stmt.condition);
        clone.thenBlock = cloneStatement(stmt.thenBlock);
        if (stmt.elseBlock != null) {
            clone.elseBlock = cloneStatement(stmt.elseBlock);
        }

        this.parents.pop();
        return clone;
    }

    @Override
    public JumpStmt visitJumpStmt(JumpStmt stmt) {
        JumpStmt clone = new JumpStmt(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        clone.type = stmt.type;

        this.parents.pop();
        return clone;
    }

    @Override
    public Literal visitLiteral(Literal expr) {
        Literal clone = new Literal(this.parents.peek());
        initBasics(expr, clone);

        clone.value = expr.value;

        return clone;
    }

    @Override
    public Return visitReturn(Return stmt) {
        Return clone = new Return(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        if (stmt.value != null) {
            clone.value = cloneExpression(stmt.value);
        }

        this.parents.pop();
        return clone;
    }

    @Override
    public Type visitType(Type type) {
        Type clone = new Type(this.parents.peek());
        initBasics(type, clone);

        clone.modifier = type.modifier;
        clone.pointer = type.pointer;
        clone.type = type.type;

        return clone;
    }

    @Override
    public UnaryExpr visitUnaryExpr(UnaryExpr expr) {
        UnaryExpr clone = new UnaryExpr(this.parents.peek());
        initBasics(expr, clone);

        this.parents.push(clone);

        clone.operator = expr.operator;
        clone.expr = cloneExpression(expr.expr);

        this.parents.pop();
        return clone;
    }

    @Override
    public While visitWhile(While stmt) {
        While clone = new While(this.parents.peek());
        initBasics(stmt, clone);

        this.parents.push(clone);

        clone.condition = cloneExpression(stmt.condition);
        clone.body = cloneStatement(stmt.body);

        this.parents.pop();
        return clone;
    }

}
