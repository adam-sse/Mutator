package parsing.ast.operations;

import java.util.Deque;
import java.util.LinkedList;

import parsing.ast.Assignment;
import parsing.ast.AstElement;
import parsing.ast.BinaryExpr;
import parsing.ast.Block;
import parsing.ast.Declaration;
import parsing.ast.DeclarationStmt;
import parsing.ast.DoWhileLoop;
import parsing.ast.EmptyStmt;
import parsing.ast.Expression;
import parsing.ast.ExpressionStmt;
import parsing.ast.File;
import parsing.ast.Function;
import parsing.ast.FunctionCall;
import parsing.ast.Identifier;
import parsing.ast.If;
import parsing.ast.Literal;
import parsing.ast.Return;
import parsing.ast.Statement;
import parsing.ast.Type;
import parsing.ast.UnaryExpr;
import parsing.ast.While;

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
        if (keepIds) {
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
    public Assignment visitAssignment(Assignment stmt) {
        Assignment clone = new Assignment(parents.peek());
        initBasics(stmt, clone);
        
        parents.push(clone);
        
        clone.variable = cloneExpression(stmt.variable);
        clone.value = cloneExpression(stmt.value);
        
        parents.pop();
        return clone;
    }

    @Override
    public BinaryExpr visitBinaryExpr(BinaryExpr expr) {
        BinaryExpr clone = new BinaryExpr(parents.peek());
        initBasics(expr, clone);
        
        parents.push(clone);
        
        clone.operator = expr.operator;
        clone.left = cloneExpression(expr.left);
        clone.right = cloneExpression(expr.right);
        
        parents.pop();
        return clone;
    }

    @Override
    public Block visitBlock(Block stmt) {
        Block clone = new Block(parents.peek());
        initBasics(stmt, clone);
        
        parents.push(clone);
        
        for (Statement st : stmt.statements) {
            clone.statements.add(cloneStatement(st));
        }
        
        parents.pop();
        return clone;
    }

    @Override
    public Declaration visitDeclaration(Declaration decl) {
        Declaration clone = new Declaration(parents.peek());
        initBasics(decl, clone);
        
        parents.push(clone);
        
        clone.type = visitType(decl.type);
        clone.identifier = decl.identifier;
        
        parents.pop();
        return clone;
    }

    @Override
    public DeclarationStmt visitDeclarationStmt(DeclarationStmt stmt) {
        DeclarationStmt clone = new DeclarationStmt(parents.peek());
        initBasics(stmt, clone);
        
        parents.push(clone);
        
        clone.decl = visitDeclaration(stmt.decl);
        
        parents.pop();
        return clone;
    }

    @Override
    public DoWhileLoop visitDoWhileLoop(DoWhileLoop stmt) {
        DoWhileLoop clone = new DoWhileLoop(parents.peek());
        initBasics(stmt, clone);
        
        parents.push(clone);
        
        clone.condition = cloneExpression(stmt.condition);
        clone.body = cloneStatement(stmt.body);
        
        parents.pop();
        return clone;
    }

    @Override
    public EmptyStmt visitEmptyStmt(EmptyStmt stmt) {
        EmptyStmt clone = new EmptyStmt(parents.peek());
        initBasics(stmt, clone);
        
        return clone;
    }

    @Override
    public ExpressionStmt visitExpressionStmt(ExpressionStmt stmt) {
        ExpressionStmt clone = new ExpressionStmt(parents.peek());
        initBasics(stmt, clone);
        
        parents.push(clone);
        
        clone.expr = cloneExpression(stmt.expr);
        
        parents.pop();
        return clone;
    }

    @Override
    public File visitFile(File file) {
        File clone = new File(parents.peek());
        initBasics(file, clone);
        
        parents.push(clone);
        
        for (Function func : file.functions) {
            clone.functions.add(visitFunction(func));
        }
        
        parents.pop();
        return clone;
    }

    @Override
    public Function visitFunction(Function func) {
        Function clone = new Function(parents.peek());
        initBasics(func, clone);
        
        parents.push(clone);
        
        clone.type = visitType(func.type);
        clone.name = func.name;
        for (Declaration param : func.parameters) {
            clone.parameters.add(visitDeclaration(param));
        }
        clone.body = visitBlock(func.body);
        
        parents.pop();
        return clone;
    }

    @Override
    public FunctionCall visitFunctionCall(FunctionCall expr) {
        FunctionCall clone = new FunctionCall(parents.peek());
        initBasics(expr, clone);
        
        parents.push(clone);
        
        clone.function = expr.function;
        
        for (Expression param : expr.params) {
            clone.params.add(cloneExpression(param));
        }
        
        parents.pop();
        return clone;
    }

    @Override
    public Identifier visitIdentifier(Identifier expr) {
        Identifier clone = new Identifier(parents.peek());
        initBasics(expr, clone);
        
        clone.identifier = expr.identifier;
        
        return clone;
    }

    @Override
    public If visitIf(If stmt) {
        If clone = new If(parents.peek());
        initBasics(stmt, clone);
        
        parents.push(clone);
        
        clone.condition = cloneExpression(stmt.condition);
        clone.thenBlock = cloneStatement(stmt.thenBlock);
        if (stmt.elseBlock != null) {
            clone.elseBlock = cloneStatement(stmt.elseBlock);
        }
        
        parents.pop();
        return clone;
    }

    @Override
    public Literal visitLiteral(Literal expr) {
        Literal clone = new Literal(parents.peek());
        initBasics(expr, clone);
        
        clone.value = expr.value;
        
        return clone;
    }

    @Override
    public Return visitReturn(Return stmt) {
        Return clone = new Return(parents.peek());
        initBasics(stmt, clone);
        
        parents.push(clone);

        if (stmt.value != null) {
            clone.value = cloneExpression(stmt.value);
        }
        
        parents.pop();
        return clone;
    }

    @Override
    public Type visitType(Type type) {
        Type clone = new Type(parents.peek());
        initBasics(type, clone);
        
        clone.modifier = type.modifier;
        clone.pointer = type.pointer;
        clone.type = type.type;
        
        return clone;
    }

    @Override
    public UnaryExpr visitUnaryExpr(UnaryExpr expr) {
        UnaryExpr clone = new UnaryExpr(parents.peek());
        initBasics(expr, clone);
        
        parents.push(clone);
        
        clone.operator = expr.operator;
        clone.expr = cloneExpression(expr.expr);
        
        parents.pop();
        return clone;
    }

    @Override
    public While visitWhile(While stmt) {
        While clone = new While(parents.peek());
        initBasics(stmt, clone);
        
        parents.push(clone);
        
        clone.condition = cloneExpression(stmt.condition);
        clone.body = cloneStatement(stmt.body);
        
        parents.pop();
        return clone;
    }
    
}
